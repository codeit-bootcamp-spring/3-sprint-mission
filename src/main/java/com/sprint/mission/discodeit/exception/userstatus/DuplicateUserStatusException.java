package com.sprint.mission.discodeit.exception.userstatus;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

/**
 * 중복된 사용자 상태 생성 시도 시 발생하는 예외
 */
public class DuplicateUserStatusException extends UserStatusException {

  public DuplicateUserStatusException() {
    super(ErrorCode.DUPLICATE_USER_STATUS);
  }

  public DuplicateUserStatusException(Map<String, Object> details) {
    super(ErrorCode.DUPLICATE_USER_STATUS, details);
  }

  public DuplicateUserStatusException(Throwable cause) {
    super(ErrorCode.DUPLICATE_USER_STATUS, cause);
  }

  public DuplicateUserStatusException(Map<String, Object> details, Throwable cause) {
    super(ErrorCode.DUPLICATE_USER_STATUS, details, cause);
  }

  // === 편의 정적 팩토리 메서드들 ===

  /**
   * 사용자 ID로 예외를 생성하는 정적 팩토리 메서드
   */
  public static DuplicateUserStatusException withUserId(UUID userId) {
    return new DuplicateUserStatusException(Map.of(
        "userId", userId,
        "duplicateField", "userId"));
  }

  /**
   * 일반적인 생성 메서드
   */
  public static DuplicateUserStatusException of() {
    return new DuplicateUserStatusException();
  }

  /**
   * 상세 정보와 함께 생성하는 메서드
   */
  public static DuplicateUserStatusException of(Map<String, Object> details) {
    return new DuplicateUserStatusException(details);
  }
}