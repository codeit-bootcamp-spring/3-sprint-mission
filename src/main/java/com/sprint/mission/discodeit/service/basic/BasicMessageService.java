package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.exception.ChannelException;
import com.sprint.mission.discodeit.exception.MessageException;
import com.sprint.mission.discodeit.exception.UserException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.command.CreateMessageCommand;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

  private final MessageRepository messageRepository;
  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final BinaryContentRepository binaryContentRepository;

  @Override
  public Message create(CreateMessageCommand command) {
    userRepository.findById(command.authorId())
        .orElseThrow(() -> UserException.notFound(command.authorId()));

    channelRepository.findById(command.channelId())
        .orElseThrow(() -> ChannelException.notFound(command.channelId()));

    Set<UUID> attachmentIds = command.attachments().stream()
        .map(attachmentRequest -> {
          String fileName = attachmentRequest.fileName();
          String contentType = attachmentRequest.contentType();
          byte[] bytes = attachmentRequest.bytes();

          BinaryContent binaryContent = BinaryContent.create(fileName, (long) bytes.length,
              contentType, bytes);
          BinaryContent createdBinaryContent = binaryContentRepository.save(binaryContent);
          return createdBinaryContent.getId();
        }).collect(Collectors.toSet());

    Message message = Message.create(command.content(),
        command.authorId(),
        command.channelId(),
        attachmentIds);

    return messageRepository.save(message);
  }

  @Override
  public Message findById(UUID messageId) {
    return messageRepository.findById(messageId)
        .orElseThrow(() -> MessageException.notFound(messageId));
  }

  @Override
  public List<Message> findAllByChannelId(UUID channelId) {
    return messageRepository.findAllByChannelId(channelId).stream()
        .filter(m -> m.getDeletedAt() == null)
        .sorted(Comparator.comparing(Message::getCreatedAt))
        .collect(Collectors.toList());
  }

  @Override
  public Message updateContent(UUID messageId, String newContent) {
    return messageRepository.findById(messageId)
        .map(message -> {
          message.updateContent(newContent);
          return messageRepository.save(message);
        }).orElseThrow(() -> MessageException.notFound(messageId));
  }

  @Override
  public Message delete(UUID messageId) {
    return messageRepository.findById(messageId)
        .filter(m -> m.getDeletedAt() == null)
        .map(m -> {
          m.getAttachmentIds().forEach(binaryContentRepository::delete);
          m.delete();
          return messageRepository.save(m);
        }).orElseThrow(() -> MessageException.notFound(messageId));
  }
}
