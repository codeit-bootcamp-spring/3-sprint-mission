package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

/**
 * 중복된 사용자 생성 시도 시 발생하는 예외
 */
public class DuplicateUserException extends UserException {

  public DuplicateUserException() {
    super(ErrorCode.DUPLICATE_USER);
  }

  public DuplicateUserException(Map<String, Object> details) {
    super(ErrorCode.DUPLICATE_USER, details);
  }

  public DuplicateUserException(Throwable cause) {
    super(ErrorCode.DUPLICATE_USER, cause);
  }

  public DuplicateUserException(Map<String, Object> details, Throwable cause) {
    super(ErrorCode.DUPLICATE_USER, details, cause);
  }

  // === 편의 정적 팩토리 메서드들 ===

  /**
   * 중복된 이메일로 예외를 생성하는 정적 팩토리 메서드
   */
  public static DuplicateUserException withEmail(String email) {
    return new DuplicateUserException(Map.of(
        "duplicateField", "email",
        "email", email));
  }

  /**
   * 중복된 사용자명으로 예외를 생성하는 정적 팩토리 메서드
   */
  public static DuplicateUserException withUsername(String username) {
    return new DuplicateUserException(Map.of(
        "duplicateField", "username",
        "username", username));
  }

  /**
   * 일반적인 생성 메서드
   */
  public static DuplicateUserException of() {
    return new DuplicateUserException();
  }

  /**
   * 상세 정보와 함께 생성하는 메서드
   */
  public static DuplicateUserException of(Map<String, Object> details) {
    return new DuplicateUserException(details);
  }
}