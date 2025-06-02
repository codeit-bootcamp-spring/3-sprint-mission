package com.sprint.mission.discodeit.dto.mapper;

import com.sprint.mission.discodeit.dto.data.*;
import com.sprint.mission.discodeit.entity.*;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class EntityDtoMapper {

  public UserDto toDto(User user) {
    return new UserDto(
        user.getId(),
        user.getUsername(),
        user.getEmail(),
        Optional.ofNullable(user.getProfile()).map(this::toDto).orElse(null),
        Optional.ofNullable(user.getUserStatus()).map(UserStatus::isOnline).orElse(null));
  }

  public BinaryContentDto toDto(BinaryContent binaryContent) {
    return new BinaryContentDto(
        binaryContent.getId(),
        binaryContent.getFileName(),
        binaryContent.getSize(),
        binaryContent.getContentType(),
        binaryContent.getBytes());
  }

  public MessageDto toDto(Message message) {
    return new MessageDto(
        message.getId(),
        message.getCreatedAt(),
        message.getUpdatedAt(),
        message.getContent(),
        message.getChannel().getId(),
        Optional.ofNullable(message.getAuthor()).map(this::toDto).orElse(null),
        message.getMessageAttachments().stream()
            .map(messageAttachment -> toDto(messageAttachment.getAttachment()))
            .collect(Collectors.toList()));
  }

  public ChannelDto toDto(Channel channel) {
    // 지연 로딩 활용 - messages 컬렉션 접근 시 필요한 경우만 로드
    Instant lastMessageAt = channel.getMessages().stream()
        .max(Comparator.comparing(Message::getCreatedAt))
        .map(Message::getCreatedAt)
        .orElse(Instant.MIN);

    List<UserDto> participants = List.of();
    if (channel.getType().equals(ChannelType.PRIVATE)) {
      // 지연 로딩 활용 - readStatuses 컬렉션 접근
      participants = channel.getReadStatuses().stream()
          .map(readStatus -> toDto(readStatus.getUser()))
          .collect(Collectors.toList());
    }

    return new ChannelDto(
        channel.getId(),
        channel.getType(),
        channel.getName(),
        channel.getDescription(),
        participants,
        lastMessageAt);
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