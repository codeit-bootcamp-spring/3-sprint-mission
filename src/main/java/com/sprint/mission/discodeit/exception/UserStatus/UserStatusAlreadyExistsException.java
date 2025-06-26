package com.sprint.mission.discodeit.exception.UserStatus;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.ErrorCode;

public class UserStatusAlreadyExistsException extends UserStatusException {

  public UserStatusAlreadyExistsException() {
    super(ErrorCode.DUPLICATE_USERSTATUS);
  }

  public UserStatusAlreadyExistsException(UserStatus userStatus) {
    super(ErrorCode.DUPLICATE_USERSTATUS,
        String.format("해당하는 유저의 status가 이미 존재합니다: Email=%s", userStatus.getUser().getEmail()));
    super.addDetails("userStatus", userStatus);
  }
}
