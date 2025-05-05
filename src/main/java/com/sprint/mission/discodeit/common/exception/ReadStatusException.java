package com.sprint.mission.discodeit.common.exception;

import java.util.UUID;

public class ReadStatusException extends BusinessException {

  public static final String READ_STATUS_NOT_FOUND = "R001";
  public static final String DUPLICATE_READ_STATUS = "R002";
  public static final String INVALID_USER_OR_CHANNEL = "R003";

  public ReadStatusException(String errorCode, String message) {
    super(errorCode, message);
  }

  /**
   * ReadStatus가 존재하지 않을 때
   */
  public static ReadStatusException notFound(UUID readStatusId) {
    return new ReadStatusException(
        READ_STATUS_NOT_FOUND,
        "읽기 상태 정보를 찾을 수 없습니다. [ReadStatusID: " + readStatusId + "]"
    );
  }

  /**
   * 동일한 UserId와 ChannelId로 이미 ReadStatus가 존재할 때
   */
  public static ReadStatusException duplicate(UUID userId, UUID channelId) {
    return new ReadStatusException(
        DUPLICATE_READ_STATUS,
        "이미 존재하는 읽기 상태 정보입니다. [UserID: " + userId + ", ChannelID: " + channelId + "]"
    );
  }

  /**
   * 유효하지 않은 User나 Channel로 요청했을 때
   */
  public static ReadStatusException invalidUserOrChannel(UUID userId, UUID channelId) {
    return new ReadStatusException(
        INVALID_USER_OR_CHANNEL,
        "유효하지 않은 사용자 또는 채널입니다. [UserID: " + userId + ", ChannelID: " + channelId + "]"
    );
  }
}
