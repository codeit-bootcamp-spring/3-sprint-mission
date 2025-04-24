package com.sprint.mission.discodeit.exception;

import java.util.UUID;

public class ChannelException extends BusinessException {

  public static final String CHANNEL_NOT_FOUND = "C001";
  public static final String PARTICIPANT_ALREADY_EXISTS = "C002";
  public static final String PARTICIPANT_NOT_FOUND = "C003";

  public ChannelException(String message, String errorCode) {
    super(message, errorCode);
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
}
