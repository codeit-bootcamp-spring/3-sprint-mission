package com.sprint.mission.discodeit.exception.auth;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class InvalidCredentialsException extends AuthException {

  public InvalidCredentialsException() {
    super(ErrorCode.UNAUTHORIZED, "일치하는 회원 정보가 없습니다.");
  }
}
