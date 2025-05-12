package com.sprint.mission.discodeit.exception;

public class AuthException extends BusinessException {

  public AuthException(ErrorCode errorCode, String message) {
    super(errorCode, message);
  }

  public static AuthException invalidCredentials() {
    return new AuthException(ErrorCode.UNAUTHORIZED, "일치하는 회원 정보가 없습니다.");
  }

  public static AuthException usernameNotFound() {
    return new AuthException(ErrorCode.INVALID_NAME, "username이 일치하지 않습니다.");
  }

  public static AuthException invalidPassword() {
    return new AuthException(ErrorCode.INVALID_PASSWORD, "password가 일치하지 않습니다.");
  }
}
