package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.assembler.MessageAssembler;
import com.sprint.mission.discodeit.dto.response.MessageResponse;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.BinaryContentCreateEvent;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.command.CreateMessageCommand;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BasicMessageService implements MessageService {

  private final MessageRepository messageRepository;
  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final ApplicationEventPublisher applicationEventPublisher;
  private final MessageAssembler messageAssembler;
  private final MessageMapper messageMapper;

  @Override
  public MessageResponse create(CreateMessageCommand command) {
    User author = userRepository.findById(command.authorId())
        .orElseThrow(() -> new UserNotFoundException(command.authorId().toString()));
    Channel channel = channelRepository.findById(command.channelId())
        .orElseThrow(() -> new ChannelNotFoundException(command.channelId().toString()));
    Message message = Message.create(command.content(), author, channel);

    command.attachments().forEach(attachment -> {
      BinaryContent binaryContent = BinaryContent.create(
          attachment.fileName(),
          (long) attachment.bytes().length,
          attachment.contentType());
      BinaryContent saved = binaryContentRepository.save(binaryContent);
      applicationEventPublisher.publishEvent(
          new BinaryContentCreateEvent(saved.getId(), attachment.bytes()));
      message.attach(saved);
    });

    Message savedMessage = messageRepository.save(message);
    applicationEventPublisher.publishEvent(
        new com.sprint.mission.discodeit.event.MessageCreatedEvent(savedMessage));
    return messageAssembler.toResponse(savedMessage);
  }

  @Override
  @Transactional(readOnly = true)
  public MessageResponse findById(UUID messageId) {
    return messageRepository.findById(messageId)
        .map(messageAssembler::toResponse)
        .orElseThrow(() -> new MessageNotFoundException(messageId.toString()));
  }

  @Override
  @Transactional(readOnly = true)
  public List<MessageResponse> findAllByChannelId(UUID channelId) {
    return messageRepository.findAllByChannelId(channelId).stream()
        .sorted(Comparator.comparing(Message::getCreatedAt))
        .map(messageAssembler::toResponse)
        .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public PageResponse<MessageResponse> findAllByChannelIdWithCursor(
      UUID channelId, Instant nextCursor, Pageable pageable) {
    Instant cursor = nextCursor != null ? nextCursor : Instant.now();

    Page<Message> messages = messageRepository
        .findByChannelIdAndCreatedAtBeforeOrderByCreatedAtDesc(
            channelId,
            cursor,
            pageable);
    List<MessageResponse> responses = messageMapper.fromEntityList(messages.getContent());

    Instant newNextCursor =
        responses.isEmpty() ? null : responses.get(responses.size() - 1).createdAt();

    return new PageResponse<>(
        responses,
        newNextCursor != null ? newNextCursor.toString() : null,
        pageable.getPageSize(),
        messages.hasNext(),
        null);
  }

  @Override
  @PreAuthorize("@messageSecurity.isAuthor(#messageId)")
  public MessageResponse updateContent(UUID messageId, String newContent) {
    return messageRepository.findById(messageId)
        .map(message -> {
          message.updateContent(newContent);
          return messageAssembler.toResponse(messageRepository.save(message));
        })
        .orElseThrow(() -> new MessageNotFoundException(messageId.toString()));
  }

  @Override
  @PreAuthorize("@messageSecurity.isAuthor(#messageId)")
  public void delete(UUID messageId) {
    messageRepository.findById(messageId)
        .orElseThrow(() -> new MessageNotFoundException(messageId.toString()));
    messageRepository.deleteById(messageId);
  }
}
