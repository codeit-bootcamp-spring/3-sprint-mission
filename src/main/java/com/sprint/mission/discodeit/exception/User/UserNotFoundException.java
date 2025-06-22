package com.sprint.mission.discodeit.exception.User;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.ErrorCode;

public class UserNotFoundException extends UserException {

  public UserNotFoundException() {
    super(ErrorCode.USER_NOT_FOUND);
  }

  public UserNotFoundException(User user) {
    super(ErrorCode.USER_NOT_FOUND, String.format("존재하지 않는 유저 입니다: Email=%s", user.getEmail()));
    super.addDetails("user", user);
  }
}
