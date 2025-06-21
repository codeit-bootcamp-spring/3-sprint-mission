package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class UserStatusNotFoundException extends UserStatusException {
  public UserStatusNotFoundException(String idOrUserId, boolean isUserId) {
    super(ErrorCode.USER_STATUS_NOT_FOUND,
        isUserId
            ? "UserStatus를 찾을 수 없습니다. [UserID: " + idOrUserId + "]"
            : "UserStatus를 찾을 수 없습니다. [ID: " + idOrUserId + "]");
  }

  public UserStatusNotFoundException() {
    super(ErrorCode.USER_STATUS_NOT_FOUND, "UserStatus를 찾을 수 없습니다.");
  }

  public UserStatusNotFoundException(String userId) {
    super(ErrorCode.USER_STATUS_NOT_FOUND, "UserStatus를 찾을 수 없습니다. [UserID: " + userId + "]");
  }
}
