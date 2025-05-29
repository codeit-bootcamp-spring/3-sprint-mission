package com.sprint.mission.discodeit.dto.mapper;

import com.sprint.mission.discodeit.dto.response.*;
import com.sprint.mission.discodeit.entity.*;

public class ResponseMapper {

  public static UserResponse toResponse(User user) {
    return new UserResponse(
        user.getId(),
        user.getCreatedAt(),
        user.getUpdatedAt(),
        user.getUsername(),
        user.getEmail(),
        user.getProfileId()
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
        message.getChannelId(),
        message.getAuthorId(),
        message.getAttachmentIds());
  }

  public static ReadStatusResponse toResponse(ReadStatus readStatus) {
    return new ReadStatusResponse(
        readStatus.getId(),
        readStatus.getCreatedAt(),
        readStatus.getUpdatedAt(),
        readStatus.getUserId(),
        readStatus.getChannelId(),
        readStatus.getLastReadAt());
  }

  public static UserStatusResponse toResponse(UserStatus userStatus) {
    return new UserStatusResponse(
        userStatus.getId(),
        userStatus.getCreatedAt(),
        userStatus.getUpdatedAt(),
        userStatus.getUserId(),
        userStatus.getLastActiveAt(),
        userStatus.isOnline());
  }

  public static BinaryContentResponse toResponse(BinaryContent binaryContent) {
    return new BinaryContentResponse(
        binaryContent.getId(),
        binaryContent.getCreatedAt(),
        binaryContent.getFileName(),
        binaryContent.getSize(),
        binaryContent.getContentType(),
        binaryContent.getBytes());
  }
}