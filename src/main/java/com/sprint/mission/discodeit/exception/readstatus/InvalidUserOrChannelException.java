package com.sprint.mission.discodeit.exception.readstatus;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class InvalidUserOrChannelException extends ReadStatusException {
  public InvalidUserOrChannelException(String userId, String channelId) {
    super(ErrorCode.READ_STATUS_INVALID_USER_OR_CHANNEL,
        "유효하지 않은 사용자 또는 채널입니다. [UserID: " + userId + ", ChannelID: " + channelId + "]");
  }
}
