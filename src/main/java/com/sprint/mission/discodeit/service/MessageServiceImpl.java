package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.common.exception.ChannelException;
import com.sprint.mission.discodeit.common.exception.UserException;
import com.sprint.mission.discodeit.dto.data.MessageResponse;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

  private final MessageRepository messageRepository;
  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final BinaryContentRepository binaryContentRepository;

  @Override
  public Message create(String content, UUID userId, UUID channelId)
      throws UserException, ChannelException {
    userRepository.findById(userId)
        .orElseThrow(() -> UserException.notFound(userId));

    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> ChannelException.notFound(channelId));

    if (channel.isNotParticipant(userId)) {
      throw ChannelException.participantNotFound(userId, channelId);
    }

    Message message = Message.create(content, userId, channelId);
    return messageRepository.save(message);
  }

  @Override
  public Message create(MessageCreateRequest request) throws ChannelException {
    userRepository.findById(request.userId())
        .orElseThrow(() -> ChannelException.notFound(request.userId()));

    Channel channel = channelRepository.findById(request.channelId())
        .orElseThrow(() -> ChannelException.notFound(request.channelId()));

    if (channel.isNotParticipant(request.userId())) {
      throw ChannelException.participantNotFound(request.userId(), request.channelId());
    }

    Message message = Message.create(request.content(), request.userId(), request.channelId());
    Message savedMessage = messageRepository.save(message);

    Set<UUID> attachmentIds = request.attachmentIds().orElse(Set.of());

    attachmentIds.forEach(attachmentId -> {
      binaryContentRepository.findById(attachmentId).ifPresent(content -> {
        content.attachToMessage(savedMessage.getId());
        binaryContentRepository.save(content);
      });
    });

    return savedMessage;
  }


  @Override
  public Optional<MessageResponse> findById(UUID id) {
    return messageRepository.findById(id)
        .map(this::toResponse);
  }

  @Override
  public List<MessageResponse> searchMessages(UUID channelId, UUID userId, String content) {
    return messageRepository.findAll().stream()
        .filter(m -> m.getDeletedAt() == null)
        .filter(m ->
            (channelId == null || m.getChannelId().equals(channelId)) &&
                (userId == null || m.getUserId().equals(userId)) &&
                (content == null || m.getContent().contains(content)))
        .sorted(Comparator.comparing(Message::getCreatedAt))
        .map(this::toResponse)
        .collect(Collectors.toList());
  }

  @Override
  public List<MessageResponse> findAllByChannelId(UUID channelId) {
    return messageRepository.findAllByChannelId(channelId).stream()
        .filter(m -> m.getDeletedAt() == null)
        .sorted(Comparator.comparing(Message::getCreatedAt))
        .map(this::toResponse)
        .collect(Collectors.toList());
  }

  @Override
  public Optional<MessageResponse> updateContent(MessageUpdateRequest request) {
    return messageRepository.findById(request.messageId())
        .map(message -> {
          message.updateContent(request.newContent());
          return messageRepository.save(message);
        })
        .map(this::toResponse);
  }

  @Override
  public Optional<MessageResponse> delete(UUID id) {
    return messageRepository.findById(id)
        .filter(m -> m.getDeletedAt() == null)
        .map(m -> {
          // 관련 첨부파일 모두 삭제
          binaryContentRepository.deleteAllByMessageId(id);

          m.delete();
          return messageRepository.save(m);
        })
        .map(this::toResponse);
  }

  @Override
  public Optional<MessageResponse> attachFilesToMessage(UUID messageId, List<UUID> attachmentIds) {
    Message message = messageRepository.findById(messageId)
        .orElseThrow(() -> new RuntimeException("Message not found"));

    attachmentIds.forEach(attachmentId -> {
      binaryContentRepository.findById(attachmentId).ifPresent(content -> {
        content.attachToMessage(messageId);
        binaryContentRepository.save(content);
      });
    });

    return Optional.of(toResponse(message));
  }

  private MessageResponse toResponse(Message message) {
    // 첨부파일 목록 조회
    List<BinaryContent> attachments = binaryContentRepository.findAllByMessageId(message.getId());

    List<MessageResponse.AttachmentResponse> attachmentResponses = attachments.stream()
        .map(content -> new MessageResponse.AttachmentResponse(
            content.getId(),
            content.getFileName(),
            content.getMimeType(),
            content.getData().length
        ))
        .collect(Collectors.toList());

    return new MessageResponse(
        message.getId(),
        message.getContent(),
        message.getUserId(),
        message.getChannelId(),
        message.getCreatedAt(),
        attachmentResponses
    );
  }
}
