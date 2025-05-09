package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.exception.ChannelException;
import com.sprint.mission.discodeit.exception.UserException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
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
public class BasicMessageService implements MessageService {

  private final MessageRepository messageRepository;
  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final BinaryContentRepository binaryContentRepository;

  @Override
  public Message create(MessageCreateRequest messageCreateRequest,
      List<BinaryContentCreateRequest> binaryContentCreateRequests) throws ChannelException {
    userRepository.findById(messageCreateRequest.userId())
        .orElseThrow(() -> UserException.notFound(messageCreateRequest.userId()));

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

    Message message = Message.create(messageCreateRequest.content(), messageCreateRequest.userId(),
        messageCreateRequest.channelId(),
        attachmentIds);

    return messageRepository.save(message);
  }

  @Override
  public Optional<Message> findById(UUID id) {
    return messageRepository.findById(id);
  }

  @Override
  public List<Message> searchMessages(UUID channelId, UUID userId, String content) {
    return messageRepository.findAll().stream()
        .filter(m -> m.getDeletedAt() == null)
        .filter(m ->
            (channelId == null || m.getChannelId().equals(channelId)) &&
                (userId == null || m.getUserId().equals(userId)) &&
                (content == null || m.getContent().contains(content)))
        .sorted(Comparator.comparing(Message::getCreatedAt))
        .collect(Collectors.toList());
  }

  @Override
  public List<Message> findAllByChannelId(UUID channelId) {
    return messageRepository.findAllByChannelId(channelId).stream()
        .filter(m -> m.getDeletedAt() == null)
        .sorted(Comparator.comparing(Message::getCreatedAt))
        .collect(Collectors.toList());
  }

  @Override
  public Optional<Message> updateContent(MessageUpdateRequest request) {
    return messageRepository.findById(request.messageId())
        .map(message -> {
          message.updateContent(request.newContent());
          return messageRepository.save(message);
        });
  }

  @Override
  public Optional<Message> delete(UUID id) {
    return messageRepository.findById(id)
        .filter(m -> m.getDeletedAt() == null)
        .map(m -> {
          binaryContentRepository.delete(id);

          m.delete();
          return messageRepository.save(m);
        });
  }
}
