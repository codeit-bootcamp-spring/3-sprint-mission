package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
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
  private final UserService userService;
  private final ChannelRepository channelRepository;
  private final BinaryContentRepository binaryContentRepository;

  private static final DateTimeFormatter DATE_FORMATTER =
      DateTimeFormatter.ofPattern("MM월 dd일 a hh시 mm분 ss초", Locale.KOREAN)
          .withZone(ZoneId.of("Asia/Seoul"));

  @Override
  public Message createMessage(MessageCreateRequest messageCreateRequest, List<BinaryContentCreateRequest> binaryContentCreateRequests) {
    UUID channelId = messageCreateRequest.channelId();
    UUID senderId = messageCreateRequest.senderId();

    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> new IllegalArgumentException("해당 채널이 존재하지 않습니다."));
    UserDto senderDTO = userService.find(senderId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저 ID입니다."));

    if (channel.getChannelMembers().size() < 2) {
      throw new IllegalStateException("채널에 최소 두 명 이상의 유저가 있어야 메시지를 보낼 수 있습니다.");
    }

    boolean isMember = channel.getChannelMembers().stream()
        .anyMatch(member -> member.getId().equals(senderId));
    if (!isMember) {
      throw new IllegalArgumentException("해당 유저는 이 채널의 멤버가 아닙니다.");
    }

    Message message = new Message(messageCreateRequest.content(), channelId, senderId);
    Message savedMessage = messageRepository.save(message);

    if (binaryContentCreateRequests != null) {
      for (BinaryContentCreateRequest fileRequest : binaryContentCreateRequests) {
        if (fileRequest.isValid()) {
          BinaryContent binaryContent = new BinaryContent(fileRequest.fileName(), senderId, savedMessage.getId(),
              fileRequest.bytes(), fileRequest.contentType());
          message.addAttachment(binaryContent.getId());
          binaryContentRepository.save(binaryContent);
        } else {
          throw new IllegalArgumentException("메세지에 첨부파일을 추가할 수 없습니다. 파일을 확인해주세요.");
        }
      }
      messageRepository.save(savedMessage);
    }

    return savedMessage;
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
        .filter(m -> m.getSenderId().equals(senderId))
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
        .filter(m -> !m.getSenderId().equals(receiverId))
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
        if (!msg.getSenderId().equals(receiverId)) {
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

    if (!msg.getSenderId().equals(request.senderId())) {
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
    if (!msg.getSenderId().equals(senderId)) {
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

    Optional<UserDto> senderOpt = userService.find(message.getSenderId());
    String senderName = senderOpt.map(UserDto::getUsername).orElse("알 수 없음");
    String senderEmail = senderOpt.map(UserDto::getEmail).orElse("이메일 없음");

    String channelName = channelRepository.findById(message.getChannelId())
        .map(Channel::getChannelName)
        .orElse("알 수 없음");

    return String.format("[%s] %s, 보낸사람: %s(%s), 내용: \"%s\"",
        channelName, formattedDate, senderName, senderEmail, message.getContent());
  }
}
