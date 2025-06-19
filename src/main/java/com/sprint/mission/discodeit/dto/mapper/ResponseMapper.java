package com.sprint.mission.discodeit.dto.mapper;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.response.*;
import com.sprint.mission.discodeit.entity.*;

import java.util.Optional;
import java.util.stream.Collectors;

public class ResponseMapper {

  public static UserResponse toResponse(User user) {
    return new UserResponse(
        user.getId(),
        user.getCreatedAt(),
        user.getUpdatedAt(),
        user.getUsername(),
        user.getEmail(),
        Optional.ofNullable(user.getProfile()).map(BinaryContent::getId).orElse(null)
    // password 제외 - 보안상 노출 안함
    );
  }

  public static UserResponse toResponse(UserDto userDto) {
    return new UserResponse(
        userDto.id(),
        userDto.createdAt(),
        userDto.updatedAt(),
        userDto.username(),
        userDto.email(),
        Optional.ofNullable(userDto.profile()).map(profile -> profile.id()).orElse(null)
    // password 제외 - 보안상 노출 안함
    );
  }

  public static ChannelResponse toResponse(Channel channel) {
    return new ChannelResponse(
        channel.getId(),
        channel.getCreatedAt(),
        channel.getUpdatedAt(),
        channel.getType(),
        channel.getName(),
        channel.getDescription());
  }

  public static MessageResponse toResponse(Message message) {
    return new MessageResponse(
        message.getId(),
        message.getCreatedAt(),
        message.getUpdatedAt(),
        message.getContent(),
        message.getChannel().getId(),
        Optional.ofNullable(message.getAuthor()).map(User::getId).orElse(null),
        message.getMessageAttachments().stream()
            .map(messageAttachment -> messageAttachment.getAttachment().getId())
            .collect(Collectors.toList()));
  }

  public static ReadStatusResponse toResponse(ReadStatus readStatus) {
    return new ReadStatusResponse(
        readStatus.getId(),
        readStatus.getCreatedAt(),
        readStatus.getUpdatedAt(),
        readStatus.getUser().getId(),
        readStatus.getChannel().getId(),
        readStatus.getLastReadAt());
  }

  public static UserStatusResponse toResponse(UserStatus userStatus) {
    return new UserStatusResponse(
        userStatus.getId(),
        userStatus.getCreatedAt(),
        userStatus.getUpdatedAt(),
        userStatus.getUser().getId(),
        userStatus.getLastActiveAt(),
        userStatus.isOnline());
  }

  public static BinaryContentResponse toResponse(BinaryContent binaryContent) {
    return new BinaryContentResponse(
        binaryContent.getId(),
        binaryContent.getCreatedAt(),
        binaryContent.getFileName(),
        binaryContent.getSize(),
        binaryContent.getContentType());
  }
}