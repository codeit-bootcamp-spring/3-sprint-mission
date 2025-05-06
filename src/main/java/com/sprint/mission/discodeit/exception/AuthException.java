package com.sprint.mission.discodeit.exception;

public class AuthException extends BusinessException {

  // 인증 관련 에러 코드 상수 정의
  public static final String INVALID_CREDENTIALS = "A001";

  public AuthException(String errorCode, String message) {
    super(errorCode, message);
  }

  // 팩토리 메서드들
  public static AuthException invalidCredentials() {
    return new AuthException(INVALID_CREDENTIALS, "일치하는 회원 정보가 없습니다.");
  }
}
