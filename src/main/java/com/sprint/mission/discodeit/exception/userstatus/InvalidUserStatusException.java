package com.sprint.mission.discodeit.exception.userstatus;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

/**
 * 잘못된 사용자 상태 조회/수정 시 발생하는 예외
 */
public class InvalidUserStatusException extends UserStatusException {

  public InvalidUserStatusException() {
    super(ErrorCode.INVALID_USER_STATUS);
  }

  public InvalidUserStatusException(Map<String, Object> details) {
    super(ErrorCode.INVALID_USER_STATUS, details);
  }

  public InvalidUserStatusException(Throwable cause) {
    super(ErrorCode.INVALID_USER_STATUS, cause);
  }

  public InvalidUserStatusException(Map<String, Object> details, Throwable cause) {
    super(ErrorCode.INVALID_USER_STATUS, details, cause);
  }

  // === 편의 정적 팩토리 메서드들 ===

  /**
   * 사용자 상태 ID로 예외를 생성하는 정적 팩토리 메서드
   */
  public static InvalidUserStatusException withUserStatusId(UUID userStatusId) {
    return new InvalidUserStatusException(Map.of("userStatusId", userStatusId));
  }

  /**
   * 사용자 ID로 예외를 생성하는 정적 팩토리 메서드
   */
  public static InvalidUserStatusException withUserId(UUID userId) {
    return new InvalidUserStatusException(Map.of("userId", userId));
  }

  /**
   * 일반적인 생성 메서드
   */
  public static InvalidUserStatusException of() {
    return new InvalidUserStatusException();
  }

  /**
   * 상세 정보와 함께 생성하는 메서드
   */
  public static InvalidUserStatusException of(Map<String, Object> details) {
    return new InvalidUserStatusException(details);
  }
}