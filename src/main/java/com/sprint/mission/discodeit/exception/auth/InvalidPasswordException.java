package com.sprint.mission.discodeit.exception.auth;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class InvalidPasswordException extends AuthException {

  public InvalidPasswordException() {
    super(ErrorCode.INVALID_INPUT, "password가 일치하지 않습니다.");
  }
}
