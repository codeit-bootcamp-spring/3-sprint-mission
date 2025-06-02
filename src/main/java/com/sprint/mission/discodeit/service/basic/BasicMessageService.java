package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional
public class BasicMessageService implements MessageService {

  private final MessageRepository messageRepository;
  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;

  @Override
  public Message create(MessageCreateRequest messageCreateRequest,
      List<BinaryContentCreateRequest> binaryContentCreateRequests) {
    UUID channelId = messageCreateRequest.channelId();
    UUID authorId = messageCreateRequest.authorId();

    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(
            () -> new CustomException.ChannelNotFoundException("Channel with id " + channelId + " does not exist"));

    User author = userRepository.findById(authorId)
        .orElseThrow(() -> new CustomException.UserNotFoundException("Author with id " + authorId + " does not exist"));

    // 첨부파일이 있는 경우 - BinaryContent 리스트 생성 후 첨부파일 포함 생성자 사용
    List<BinaryContent> attachments = binaryContentCreateRequests.stream()
        .map(attachmentRequest -> {
          String fileName = attachmentRequest.fileName();
          String contentType = attachmentRequest.contentType();
          byte[] bytes = attachmentRequest.bytes();
          return new BinaryContent(fileName, (long) bytes.length, contentType, bytes);
        })
        .collect(Collectors.toList());

    String content = messageCreateRequest.content();
    Message message = new Message(content, channel, author, attachments);

    return messageRepository.save(message);
  }

  @Override
  public Message create(MessageCreateRequest messageCreateRequest) {
    UUID channelId = messageCreateRequest.channelId();
    UUID authorId = messageCreateRequest.authorId();

    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(
            () -> new CustomException.ChannelNotFoundException("Channel with id " + channelId + " does not exist"));

    User author = userRepository.findById(authorId)
        .orElseThrow(() -> new CustomException.UserNotFoundException("Author with id " + authorId + " does not exist"));

    // 첨부파일이 없는 경우
    String content = messageCreateRequest.content();
    Message message = new Message(content, channel, author);

    return messageRepository.save(message);
  }

  @Override
  @Transactional(readOnly = true)
  public Message find(UUID messageId) {
    return messageRepository.findById(messageId)
        .orElseThrow(
            () -> new CustomException.MessageNotFoundException("Message with id " + messageId + " not found"));
  }

  @Override
  @Transactional(readOnly = true)
  public List<Message> findAllByChannelId(UUID channelId) {
    return messageRepository.findAllByChannelId(channelId);
  }

  @Override
  public Message update(UUID messageId, MessageUpdateRequest request) {
    String newContent = request.newContent();

    Message message = messageRepository.findById(messageId)
        .orElseThrow(
            () -> new CustomException.MessageNotFoundException("Message with id " + messageId + " not found"));

    message.update(newContent);

    return message;
  }

  @Override
  public void delete(UUID messageId) {
    Message message = messageRepository.findById(messageId)
        .orElseThrow(
            () -> new CustomException.MessageNotFoundException("Message with id " + messageId + " not found"));

    messageRepository.delete(message);
  }
}
