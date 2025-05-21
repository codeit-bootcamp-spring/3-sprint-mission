package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
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
  public Message create(MessageCreateRequest messageCreateRequest,
      List<BinaryContentCreateRequest> binaryContentCreateRequests) throws ChannelException {
    userRepository.findById(messageCreateRequest.authorId())
        .orElseThrow(() -> UserException.notFound(messageCreateRequest.authorId()));

    channelRepository.findById(messageCreateRequest.channelId())
        .orElseThrow(() -> ChannelException.notFound(messageCreateRequest.channelId()));

    Set<UUID> attachmentIds = binaryContentCreateRequests.stream()
        .map(attachmentRequest -> {
          String fileName = attachmentRequest.fileName();
          String contentType = attachmentRequest.contentType();
          byte[] bytes = attachmentRequest.bytes();

          BinaryContent binaryContent = BinaryContent.create(fileName, (long) bytes.length,
              contentType, bytes);
          BinaryContent createdBinaryContent = binaryContentRepository.save(binaryContent);
          return createdBinaryContent.getId();
        }).collect(Collectors.toSet());

    Message message = Message.create(messageCreateRequest.content(),
        messageCreateRequest.authorId(),
        messageCreateRequest.channelId(),
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
  public Message updateContent(UUID messageId, MessageUpdateRequest request) {
    return messageRepository.findById(messageId)
        .map(message -> {
          message.updateContent(request.newContent());
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
