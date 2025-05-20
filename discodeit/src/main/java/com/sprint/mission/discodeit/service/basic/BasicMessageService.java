package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.CreateBinaryContentRequest;
import com.sprint.mission.discodeit.dto.message.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.message.UpdateMessageRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

  private final MessageRepository messageRepository;
  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final UserStatusRepository userStatusRepository;


  @Override
  public Message create(CreateMessageRequest request,
      List<CreateBinaryContentRequest> binaryContentRequests
  ) {
    UUID channelId = request.channelId();
    UUID authorId = request.authorId();

    if (request.channelId() == null || !channelRepository.existsById(
        request.channelId())) {  // 피드백 1 - 이상한 파트 수정
      throw new IllegalArgumentException("존재하지 않는 채널입니다!");
    }
    if (request.authorId() == null || !userRepository.existsById(
        request.authorId())) {  // 피드백 1 - 이상한 파트 수정
      throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
    }

    List<UUID> attachmentIds = binaryContentRequests.stream()
        .map(attachmentRequest -> {
          String fileName = attachmentRequest.fileName();
          String type = attachmentRequest.type();
          byte[] bytes = attachmentRequest.bytes();

          BinaryContent binaryContent = new BinaryContent(fileName, type, (long) bytes.length,
              bytes);
          BinaryContent createdBinaryContent = binaryContentRepository.save(binaryContent);
          return createdBinaryContent.getId();
        })
        .toList();

    String content = request.content();
    Message message = new Message(
        content,
        channelId,
        authorId,
        attachmentIds
    );
    updateUserStatus(userStatusRepository, message.getAuthorId());
    return messageRepository.save(message);
  }

  @Override
  public Message find(UUID messageId) {
    Message message = messageRepository.findById(messageId)
        .orElseThrow(
            () -> new NoSuchElementException("Message with id " + messageId + " not found"));
    updateUserStatus(userStatusRepository, message.getAuthorId());
    return message;
  }

  @Override
  public List<Message> findAllByChannelId(UUID channelId) {
    return messageRepository.findAllByChannelId(channelId)
        .stream()
        .toList();
  }

  @Override
  public Message update(UUID messageId,
      UpdateMessageRequest updateMessageRequest) {
    Message message = messageRepository.findById(messageId)
        .orElseThrow(
            () -> new NoSuchElementException("Message with id " + messageId + " not found"));

    if (updateMessageRequest.newContent() == null || updateMessageRequest.newContent().isBlank()) {
      throw new IllegalArgumentException("빈 메시지는 전송할 수 없습니다.");
    }

    message.update(updateMessageRequest.newContent());
    updateUserStatus(userStatusRepository, message.getAuthorId());
    return messageRepository.save(message);
  }

  @Override
  public void delete(UUID messageId) {
    Message message = messageRepository.findById(messageId)
        .orElseThrow(
            () -> new NoSuchElementException("Message with id " + messageId + " not found"));

    message.getAttachmentIds().forEach(binaryContentRepository::deleteById);
    messageRepository.deleteById(messageId);
    updateUserStatus(userStatusRepository, message.getAuthorId());
    System.out.println("delete message : " + messageId + " success.");
  }


  private void updateUserStatus(UserStatusRepository userStatusRepo, UUID userId) {
    UserStatus userStatus = userStatusRepo.findByUserId(userId)
        .orElseThrow(() -> new NoSuchElementException("[error] 존재하지 않는 User ID입니다."));
    userStatus.update(Instant.now());
  }

}
