package com.sprint.mission.discodeit.exception.User;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class InvalidCredentialsException extends UserException {

  public InvalidCredentialsException() {
    super(ErrorCode.INVALID_CREDENTIALS);
  }

  public InvalidCredentialsException(String userName, String password) {
    super(ErrorCode.INVALID_CREDENTIALS,
        String.format("잘못된 비밀번호입니다: UserName=%s, Password=%s", userName, password));
    super.addDetails("userName", userName);
    super.addDetails("password", password);
  }
}
