package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

/**
 * 로그인 시 비밀번호가 틀린 경우 발생하는 예외
 */
public class InvalidPasswordException extends UserException {

  public InvalidPasswordException() {
    super(ErrorCode.INVALID_PASSWORD);
  }

  public InvalidPasswordException(Map<String, Object> details) {
    super(ErrorCode.INVALID_PASSWORD, details);
  }

  public InvalidPasswordException(Throwable cause) {
    super(ErrorCode.INVALID_PASSWORD, cause);
  }

  public InvalidPasswordException(Map<String, Object> details, Throwable cause) {
    super(ErrorCode.INVALID_PASSWORD, details, cause);
  }

  // === 편의 정적 팩토리 메서드들 ===

  /**
   * 사용자명으로 예외를 생성하는 정적 팩토리 메서드
   */
  public static InvalidPasswordException withUsername(String username) {
    return new InvalidPasswordException(Map.of("username", username));
  }

  /**
   * 일반적인 생성 메서드
   */
  public static InvalidPasswordException of() {
    return new InvalidPasswordException();
  }

  /**
   * 상세 정보와 함께 생성하는 메서드
   */
  public static InvalidPasswordException of(Map<String, Object> details) {
    return new InvalidPasswordException(details);
  }
}