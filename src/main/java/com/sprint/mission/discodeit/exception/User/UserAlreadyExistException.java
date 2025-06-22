package com.sprint.mission.discodeit.exception.User;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.ErrorCode;

public class UserAlreadyExistException extends UserException {

  public UserAlreadyExistException() {
    super(ErrorCode.DUPLICATE_EMAIL_NAME);
  }

  public UserAlreadyExistException(User user) {
    super(ErrorCode.DUPLICATE_EMAIL_NAME,
        String.format("이미 사용중인 이메일/유저 이름 입니다: Email=%s, Name=%s", user.getEmail(),
            user.getUsername()));
    super.addDetails("user", user);
  }
}
