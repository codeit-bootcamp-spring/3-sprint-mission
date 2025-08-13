package com.sprint.mission.discodeit.exception.userstatus;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

/**
 * 사용자 상태를 찾을 수 없을 때 발생하는 예외
 */
public class UserStatusNotFoundException extends UserStatusException {

  public UserStatusNotFoundException() {
    super(ErrorCode.USER_STATUS_NOT_FOUND);
  }

  public UserStatusNotFoundException(Map<String, Object> details) {
    super(ErrorCode.USER_STATUS_NOT_FOUND, details);
  }

  public UserStatusNotFoundException(Throwable cause) {
    super(ErrorCode.USER_STATUS_NOT_FOUND, cause);
  }

  public UserStatusNotFoundException(Map<String, Object> details, Throwable cause) {
    super(ErrorCode.USER_STATUS_NOT_FOUND, details, cause);
  }

  // === 편의 정적 팩토리 메서드들 ===

  /**
   * 사용자 상태 ID로 예외를 생성하는 정적 팩토리 메서드
   */
  public static UserStatusNotFoundException withUserStatusId(UUID userStatusId) {
    return new UserStatusNotFoundException(Map.of("userStatusId", userStatusId));
  }

  /**
   * 사용자 ID로 예외를 생성하는 정적 팩토리 메서드
   */
  public static UserStatusNotFoundException withUserId(UUID userId) {
    return new UserStatusNotFoundException(Map.of("userId", userId));
  }

  /**
   * 일반적인 생성 메서드
   */
  public static UserStatusNotFoundException of() {
    return new UserStatusNotFoundException();
  }

  /**
   * 상세 정보와 함께 생성하는 메서드
   */
  public static UserStatusNotFoundException of(Map<String, Object> details) {
    return new UserStatusNotFoundException(details);
  }
}