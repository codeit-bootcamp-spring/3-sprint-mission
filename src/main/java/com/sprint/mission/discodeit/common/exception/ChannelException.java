package com.sprint.mission.discodeit.common.exception;

import java.util.UUID;

public class ChannelException extends BusinessException {

  public static final String CHANNEL_NOT_FOUND = "C001";
  public static final String PARTICIPANT_ALREADY_EXISTS = "C002";
  public static final String PARTICIPANT_NOT_FOUND = "C003";
  public static final String CAN_NOT_UPDATE_PRIVATE_CHANNEL = "C004";

  public ChannelException(String errorCode, String message) {
    super(errorCode, message);
  }

  public static ChannelException notFound(UUID channelId) {
    return new ChannelException(CHANNEL_NOT_FOUND, "채널을 찾을 수 없습니다. [ChannelID: " + channelId + "]");
  }

  public static ChannelException participantAlreadyExists(UUID userId, UUID channelId) {
    return new ChannelException(PARTICIPANT_ALREADY_EXISTS,
        "이미 채널에 참여 중인 사용자입니다. [UserID: " + userId + ", ChannelID: " + channelId + "]"
    );
  }

  public static ChannelException participantNotFound(UUID userId, UUID channelId) {
    return new ChannelException(PARTICIPANT_NOT_FOUND,
        "채널에서 사용자를 찾을 수 없습니다. [UserID: " + userId + ", ChannelID: " + channelId + "]"
    );
  }

  public static ChannelException cannotUpdatePrivateChannel(UUID channelId) {
    return new ChannelException(CAN_NOT_UPDATE_PRIVATE_CHANNEL,
        "비공개 채널은 수정할 수 없습니다. [ChannelID: " + channelId + "]"
    );
  }
}
