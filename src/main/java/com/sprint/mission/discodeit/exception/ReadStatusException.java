package com.sprint.mission.discodeit.exception;

import java.text.MessageFormat;
import java.util.UUID;

public class ReadStatusException extends BusinessException {

  public ReadStatusException(ErrorCode errorCode) {
    super(errorCode, errorCode.getMessage());
  }

  public ReadStatusException(ErrorCode errorCode, String message) {
    super(errorCode, message);
  }

  public static ReadStatusException notFound(UUID readStatusId) {
    return new ReadStatusException(
        ErrorCode.NOT_FOUND,
        MessageFormat.format("읽기 상태 정보를 찾을 수 없습니다. [ReadStatusID: {0}]", readStatusId)
    );
  }

  public static ReadStatusException duplicate(UUID userId, UUID channelId) {
    return new ReadStatusException(
        ErrorCode.ALREADY_EXISTS,
        MessageFormat.format("이미 존재하는 읽기 상태 정보입니다. [UserID: {0}, ChannelID: {1}]", userId,
            channelId)
    );
  }

  public static ReadStatusException invalidUserOrChannel(UUID userId, UUID channelId) {
    return new ReadStatusException(
        ErrorCode.NOT_FOUND,
        MessageFormat.format("유효하지 않은 사용자 또는 채널입니다. [UserID: {0}, ChannelID: {1}]", userId,
            channelId)
    );
  }
}
