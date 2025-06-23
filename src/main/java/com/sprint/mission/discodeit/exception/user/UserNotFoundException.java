package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class UserNotFoundException extends UserException {
  public UserNotFoundException(String userId) {
    super(ErrorCode.USER_NOT_FOUND, "해당 사용자를 찾을 수 없습니다. [UserID: " + userId + "]");
  }

  public UserNotFoundException() {
    super(ErrorCode.USER_NOT_FOUND, "해당 사용자를 찾을 수 없습니다.");
  }
}
