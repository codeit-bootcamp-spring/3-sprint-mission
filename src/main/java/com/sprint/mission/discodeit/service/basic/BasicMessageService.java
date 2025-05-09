package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.MessageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BasicMessageService implements MessageService {

  private final MessageRepository messageRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;


  @Override
  public MessageResponse create(
      MessageCreateRequest request,
      List<BinaryContentCreateRequest> binaryContentCreateRequests
  ) {
    UUID channelId = request.channelId();
    UUID authorId = request.userId();

    if (!channelRepository.existsById(channelId)) {
      throw new NoSuchElementException("Channel with id " + channelId + " does not exist");
    }

    if (!userRepository.existsById(authorId)) {
      throw new NoSuchElementException("User with id " + authorId + " does not exist");
    }

    List<UUID> attachmentIds = binaryContentCreateRequests.stream()
        .map(file -> {
          BinaryContent binaryContent = new BinaryContent(
              file.filename(),
              (long) file.data().length,
              file.contentType(),
              file.data()
          );
          return binaryContentRepository.save(binaryContent).getId();
        })
        .toList();

    Message message = new Message(
        request.content(),
        channelId,
        authorId,
        attachmentIds
    );

    Message saved = messageRepository.save(message);
    return toResponse(saved);
  }


  @Override
  public MessageResponse find(UUID messageId) {
    Message message = messageRepository.findById(messageId)
        .orElseThrow(
            () -> new NoSuchElementException("Message with id " + messageId + " not found"));
    return toResponse(message);
  }

  @Override
  public List<Message> findAllByChannelId(UUID channelId) {
    return messageRepository.findAllByChannelId(channelId);
  }

  @Override
  public MessageResponse update(UUID messageId, MessageUpdateRequest request) {
    Message message = messageRepository.findById(messageId)
        .orElseThrow(
            () -> new NoSuchElementException("Message with id " + messageId + " not found"));
    message.update(request.NewContent());
    return toResponse(messageRepository.save(message));
  }

  @Override
  public MessageResponse delete(UUID messageId) {
    Message message = messageRepository.findById(messageId)
        .orElseThrow(
            () -> new NoSuchElementException("Message with id " + messageId + " not found"));

    message.getAttachmentIds().forEach(binaryContentRepository::deleteById);
    messageRepository.deleteById(messageId);
    return toResponse(message);
  }

  private MessageResponse toResponse(Message message) {
    return new MessageResponse(
        message.getId(),
        message.getChannelId(),
        message.getAuthorId(),
        message.getContent(),
        message.getAttachmentIds(),
        message.getCreatedAt(),
        message.getUpdatedAt()
    );
  }
}
