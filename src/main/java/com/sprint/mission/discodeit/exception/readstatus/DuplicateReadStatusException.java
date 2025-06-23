package com.sprint.mission.discodeit.exception.readstatus;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class DuplicateReadStatusException extends ReadStatusException {
  public DuplicateReadStatusException(String userId, String channelId) {
    super(ErrorCode.READ_STATUS_ALREADY_EXISTS,
        "이미 존재하는 읽기 상태 정보입니다. [UserID: " + userId + ", ChannelID: " + channelId + "]");
  }
}
