package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class DuplicateUserStatusException extends UserStatusException {
  public DuplicateUserStatusException(String userId) {
    super(ErrorCode.USER_STATUS_ALREADY_EXISTS, "이미 존재하는 UserStatus입니다. [UserID: " + userId + "]");
  }
}
