package com.sprint.mission.discodeit.exception;

import java.text.MessageFormat;
import java.util.UUID;

public class ChannelException extends BusinessException {

  public ChannelException(ErrorCode errorCode) {
    super(errorCode, errorCode.getMessage());
  }

  public ChannelException(ErrorCode errorCode, String message) {
    super(errorCode, message);
  }

  public static ChannelException notFound(UUID channelId) {
    return new ChannelException(
        ErrorCode.NOT_FOUND,
        MessageFormat.format("채널을 찾을 수 없습니다. [ChannelID: {0}]", channelId)
    );
  }

  public static ChannelException participantAlreadyExists(UUID userId, UUID channelId) {
    return new ChannelException(
        ErrorCode.ALREADY_EXISTS,
        MessageFormat.format("이미 채널에 참여 중인 사용자입니다. [UserID: {0}]", channelId)
    );
  }

  public static ChannelException participantNotFound(UUID userId, UUID channelId) {
    return new ChannelException(
        ErrorCode.NOT_FOUND,
        MessageFormat.format("채널에서 사용자를 찾을 수 없습니다. [UserID: {0}, ChannelID: {1}]", userId,
            channelId)
    );
  }

  public static ChannelException cannotUpdatePrivateChannel(UUID channelId) {
    return new ChannelException(
        ErrorCode.FORBIDDEN,
        MessageFormat.format("비공개 채널은 수정할 수 없습니다. [ChannelID: {0}]", channelId)
    );
  }
}
