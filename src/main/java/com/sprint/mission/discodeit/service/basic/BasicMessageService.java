package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional
public class BasicMessageService implements MessageService {

  private final MessageRepository messageRepository;
  //
  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  //
  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentStorage binaryContentStorage;


  @Override
  public Message create(MessageCreateRequest messageCreateRequest,
      List<BinaryContentCreateRequest> binaryContentCreateRequests) {

    Channel channel = channelRepository.findById(messageCreateRequest.channelId())
        .orElseThrow(() -> new NoSuchElementException(
            "Channel with id " + messageCreateRequest.channelId() + " not found"));

    User author = userRepository.findById(messageCreateRequest.authorId())
        .orElseThrow(() -> new NoSuchElementException(
            "User with id " + messageCreateRequest.authorId() + " not found"));

    Message message = new Message(messageCreateRequest.content(), channel, author);

    for (BinaryContentCreateRequest request : binaryContentCreateRequests) {
      BinaryContent binaryContent = new BinaryContent(
          request.fileName(),
          (long) request.bytes().length,
          request.contentType()
      );
      
      binaryContentStorage.put(binaryContent.getId(), request.bytes());

      message.getAttachments().add(binaryContent);
    }

    message = messageRepository.save(message);
    return message;

  }

  @Override
  @Transactional(Transactional.TxType.SUPPORTS)
  public Message find(UUID messageId) {
    return messageRepository.findById(messageId)
        .orElseThrow(
            () -> new NoSuchElementException("Message with id " + messageId + " not found"));
  }

  @Override
  @Transactional(Transactional.TxType.SUPPORTS)
  public List<Message> findAllByChannelId(UUID channelId) {
    return messageRepository.findAllByChannelId(channelId).stream()
        .toList();
  }

  @Override
  public Message update(UUID messageId, MessageUpdateRequest request) {
    // 유효성
    Message message = messageRepository.findById(messageId)
        .orElseThrow(
            () -> new NoSuchElementException("Message with id " + messageId + " not found"));

    // Update
    message.update(request.newContent());

    return message;
  }

  @Override
  public void delete(UUID messageId) {
    // 유효성
    Message message = messageRepository.findById(messageId)
        .orElseThrow(
            () -> new NoSuchElementException("Message with id " + messageId + " not found"));
    messageRepository.delete(message);
  }
}