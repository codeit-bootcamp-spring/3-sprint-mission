package com.sprint.mission.discodeit.exception.User;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.ErrorCode;

public class InvalidPasswordException extends UserException {

  public InvalidPasswordException() {
    super(ErrorCode.INVALID_PASSWORD);
  }

  public InvalidPasswordException(User user, String password) {
    super(ErrorCode.INVALID_PASSWORD,
        String.format("잘못된 비밀번호입니다: Email=%s, Password=%s", user.getEmail(), password));
  }
}
