package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

/**
 * 사용자를 찾을 수 없을 때 발생하는 예외
 */
public class UserNotFoundException extends UserException {

  public UserNotFoundException() {
    super(ErrorCode.USER_NOT_FOUND);
  }

  public UserNotFoundException(Map<String, Object> details) {
    super(ErrorCode.USER_NOT_FOUND, details);
  }

  public UserNotFoundException(Throwable cause) {
    super(ErrorCode.USER_NOT_FOUND, cause);
  }

  public UserNotFoundException(Map<String, Object> details, Throwable cause) {
    super(ErrorCode.USER_NOT_FOUND, details, cause);
  }

  // === 편의 정적 팩토리 메서드들 ===

  /**
   * 사용자 ID로 예외를 생성하는 정적 팩토리 메서드
   */
  public static UserNotFoundException withUserId(UUID userId) {
    return new UserNotFoundException(Map.of("userId", userId));
  }

  /**
   * 사용자명으로 예외를 생성하는 정적 팩토리 메서드
   */
  public static UserNotFoundException withUsername(String username) {
    return new UserNotFoundException(Map.of("username", username));
  }

  /**
   * 이메일로 예외를 생성하는 정적 팩토리 메서드
   */
  public static UserNotFoundException withEmail(String email) {
    return new UserNotFoundException(Map.of("email", email));
  }

  /**
   * 일반적인 생성 메서드
   */
  public static UserNotFoundException of() {
    return new UserNotFoundException();
  }

  /**
   * 상세 정보와 함께 생성하는 메서드
   */
  public static UserNotFoundException of(Map<String, Object> details) {
    return new UserNotFoundException(details);
  }
}