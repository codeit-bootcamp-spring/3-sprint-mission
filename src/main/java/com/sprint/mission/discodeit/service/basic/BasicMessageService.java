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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

  private final MessageRepository messageRepository;
  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final BinaryContentRepository binaryContentRepository;

  private static final DateTimeFormatter DATE_FORMATTER =
      DateTimeFormatter.ofPattern("MM월 dd일 a hh시 mm분 ss초", Locale.KOREAN)
          .withZone(ZoneId.of("Asia/Seoul"));

  @Override
  public Message createMessage(MessageCreateRequest messageCreateRequest,
      List<BinaryContentCreateRequest> binaryContentCreateRequests) {
    UUID channelId = messageCreateRequest.channelId();
    UUID authorId = messageCreateRequest.authorId();

    if (!channelRepository.existsById(channelId)) {
      throw new NoSuchElementException("해당 채널이 존재하지 않습니다.");
    }

    if (!userRepository.existsById(authorId)) {
      throw new NoSuchElementException("존재하지 않는 유저 ID입니다.");
    }

    List<UUID> attachmentIds = new ArrayList<>();
    if (binaryContentCreateRequests != null) {
      for (BinaryContentCreateRequest fileRequest : binaryContentCreateRequests) {
        if (!fileRequest.isValid()) {
          throw new IllegalArgumentException("메세지에 첨부파일을 추가할 수 없습니다. 파일을 확인해주세요.");
        }

        BinaryContent binaryContent = new BinaryContent(
            fileRequest.fileName(),
            authorId,
            fileRequest.bytes(),
            fileRequest.contentType()
        );
        BinaryContent saved = binaryContentRepository.save(binaryContent);
        attachmentIds.add(saved.getId());
      }
    }
    /*BinaryContent(첨부파일) 객체가 자신이 속한 Message(메시지)를 참조하도록 설계했지만,
    첨부파일 기준으로 메시지를 역으로 조회할 필요가 없다고 생각해서
    단방향 참조로 구조를 변경하였습니다. Message가 첨부파일의 ID만 가지고 있는 구조*/
    Message message = new Message(
        messageCreateRequest.content(),
        channelId,
        authorId,
        attachmentIds
    );

    return messageRepository.save(message);
  }

  @Override
  public List<Message> findAllByChannelId(UUID channelId) {
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> new IllegalArgumentException("채널이 존재하지 않습니다."));

    return messageRepository.findByChannelId(channelId);
  }

  @Override
  public List<Message> getMessagesBySenderInChannel(UUID channelId, UUID senderId) {
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> new IllegalArgumentException("채널이 존재하지 않습니다."));
    if (channel.getChannelMembers().stream().noneMatch(u -> u.getId().equals(senderId))) {
      throw new IllegalArgumentException("해당 유저는 이 채널의 멤버가 아닙니다.");
    }

    return messageRepository.findByChannelId(channelId).stream()
        .filter(m -> m.getAuthorId().equals(senderId))
        .toList();
  }

  @Override
  public List<Message> getMessagesByReceiverInChannel(UUID channelId, UUID receiverId) {
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> new IllegalArgumentException("채널이 존재하지 않습니다."));
    if (channel.getChannelMembers().stream().noneMatch(u -> u.getId().equals(receiverId))) {
      throw new IllegalArgumentException("해당 유저는 이 채널의 멤버가 아닙니다.");
    }

    return messageRepository.findByChannelId(channelId).stream()
        .filter(m -> !m.getAuthorId().equals(receiverId))
        .toList();
  }

  @Override
  public List<Message> getAllSentMessages(UUID senderId) {
    return messageRepository.findBySenderId(senderId);
  }

  @Override
  public List<Message> getAllReceivedMessages(UUID receiverId) {
    List<UUID> myChannels = channelRepository.findAll().stream()
        .filter(ch -> ch.getChannelMembers().stream().anyMatch(u -> u.getId().equals(receiverId)))
        .map(Channel::getId)
        .toList();

    List<Message> received = new ArrayList<>();
    for (UUID chId : myChannels) {
      List<Message> messages = messageRepository.findByChannelId(chId);
      for (Message msg : messages) {
        if (!msg.getAuthorId().equals(receiverId)) {
          received.add(msg);
        }
      }
    }
    return received;
  }

  @Override
  public Message updateMessage(UUID messageId, MessageUpdateRequest request) {
    Message msg = messageRepository.findById(messageId)
        .orElseThrow(() -> new IllegalArgumentException("메시지를 찾을 수 없습니다."));

    if (!msg.getAuthorId().equals(request.senderId())) {
      throw new SecurityException("해당 메시지를 수정할 권한이 없습니다.");
    }

    msg.updateContent(request.newContent());
    messageRepository.save(msg);
    return msg;
  }

  @Override
  public void deleteMessage(UUID messageId, UUID senderId) {
    Message msg = messageRepository.findById(messageId)
        .orElseThrow(() -> new IllegalArgumentException("메시지를 찾을 수 없습니다."));
    if (!msg.getAuthorId().equals(senderId)) {
      throw new SecurityException("해당 메시지를 삭제할 권한이 없습니다.");
    }

    // BinaryContent들도 삭제
    for (UUID attachmentId : msg.getAttachmentIds()) {
      binaryContentRepository.delete(attachmentId);
    }

    messageRepository.delete(messageId);
  }


  public Optional<Message> getMessageById(UUID messageId) {
    return messageRepository.findById(messageId);
  }

  public String formatMessage(Message message) {
    boolean isEdited = !message.getCreatedAt().equals(message.getUpdatedAt());
    Instant timestamp = isEdited ? message.getUpdatedAt() : message.getCreatedAt();

    String formattedDate = DATE_FORMATTER.format(timestamp);

    Optional<User> senderOpt = userRepository.findById(message.getAuthorId());
    String senderName = senderOpt.map(User::getUsername).orElse("알 수 없음");
    String senderEmail = senderOpt.map(User::getEmail).orElse("이메일 없음");

    String channelName = channelRepository.findById(message.getChannelId())
        .map(Channel::getName)
        .orElse("알 수 없음");

    return String.format("[%s] %s, 보낸사람: %s(%s), 내용: \"%s\"",
        channelName, formattedDate, senderName, senderEmail, message.getContent());
  }
}
