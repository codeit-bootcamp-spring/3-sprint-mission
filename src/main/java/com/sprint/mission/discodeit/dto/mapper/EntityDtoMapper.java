package com.sprint.mission.discodeit.dto.mapper;

import com.sprint.mission.discodeit.dto.data.*;
import com.sprint.mission.discodeit.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Slf4j
public class EntityDtoMapper {

  public UserDto toDto(User user) {
    return new UserDto(
        user.getId(),
        user.getCreatedAt(),
        user.getUpdatedAt(),
        user.getUsername(),
        user.getEmail(),
        Optional.ofNullable(user.getProfile()).map(this::toDto).orElse(null),
        Optional.ofNullable(user.getUserStatus()).map(UserStatus::isOnline).orElse(false));
  }

  public BinaryContentDto toDto(BinaryContent binaryContent) {
    return new BinaryContentDto(
        binaryContent.getId(),
        binaryContent.getFileName(),
        binaryContent.getSize(),
        binaryContent.getContentType());
  }

  public MessageDto toDto(Message message) {
    return new MessageDto(
        message.getId(),
        message.getCreatedAt(),
        message.getUpdatedAt(),
        message.getContent(),
        message.getChannel().getId(),
        Optional.ofNullable(message.getAuthor()).map(this::toDto).orElse(null),
        safeMapAttachments(message));
  }

  public ChannelDto toDto(Channel channel) {
    return new ChannelDto(
        channel.getId(),
        channel.getType(),
        channel.getName(),
        channel.getDescription(),
        safeMapParticipants(channel),
        safeGetLastMessageAt(channel));
  }

  public ReadStatusDto toDto(ReadStatus readStatus) {
    return new ReadStatusDto(
        readStatus.getId(),
        readStatus.getUser().getId(),
        readStatus.getChannel().getId(),
        readStatus.getLastReadAt());
  }

  public UserStatusDto toDto(UserStatus userStatus) {
    return new UserStatusDto(
        userStatus.getId(),
        userStatus.getUser().getId(),
        userStatus.getLastActiveAt());
  }

  // 안전한 첨부파일 매핑
  private List<BinaryContentDto> safeMapAttachments(Message message) {
    try {
      if (message.getMessageAttachments() != null) {
        return message.getMessageAttachments().stream()
            .map(messageAttachment -> toDto(messageAttachment.getAttachment()))
            .collect(Collectors.toList());
      }
    } catch (Exception e) {
      log.warn("첨부파일 매핑 실패 - 메시지 ID: {}, 오류: {}", message.getId(), e.getMessage());
    }
    return Collections.emptyList();
  }

  // 안전한 참가자 매핑 (비공개 채널용)
  private List<UserDto> safeMapParticipants(Channel channel) {
    if (channel.getType().isPublic()) {
      return Collections.emptyList();
    }

    try {
      if (channel.getReadStatuses() != null) {
        return channel.getReadStatuses().stream()
            .map(readStatus -> toDto(readStatus.getUser()))
            .collect(Collectors.toList());
      }
    } catch (Exception e) {
      log.warn("참가자 매핑 실패 - 채널 ID: {}, 오류: {}", channel.getId(), e.getMessage());
    }
    return Collections.emptyList();
  }

  // 안전한 마지막 메시지 시간 조회
  private Instant safeGetLastMessageAt(Channel channel) {
    try {
      if (channel.getMessages() != null && !channel.getMessages().isEmpty()) {
        return channel.getMessages().stream()
            .max(Comparator.comparing(Message::getCreatedAt))
            .map(Message::getCreatedAt)
            .orElse(channel.getCreatedAt());
      }
    } catch (Exception e) {
      log.warn("마지막 메시지 시간 조회 실패 - 채널 ID: {}, 오류: {}", channel.getId(), e.getMessage());
    }
    return channel.getCreatedAt();
  }

  // List 매핑을 위한 편의 메소드들
  public List<UserDto> toUserDtoList(List<User> users) {
    return users.stream().map(this::toDto).collect(Collectors.toList());
  }

  public List<MessageDto> toMessageDtoList(List<Message> messages) {
    return messages.stream().map(this::toDto).collect(Collectors.toList());
  }

  public List<ChannelDto> toChannelDtoList(List<Channel> channels) {
    return channels.stream().map(this::toDto).collect(Collectors.toList());
  }

  public List<ReadStatusDto> toReadStatusDtoList(List<ReadStatus> readStatuses) {
    return readStatuses.stream().map(this::toDto).collect(Collectors.toList());
  }

  public List<UserStatusDto> toUserStatusDtoList(List<UserStatus> userStatuses) {
    return userStatuses.stream().map(this::toDto).collect(Collectors.toList());
  }
}