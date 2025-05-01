package com.sprint.mission.discodeit.fixture;

import com.sprint.mission.discodeit.entity.UserStatus;
import java.lang.reflect.Field;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

/**
 * UserStatus 테스트를 위한 Fixture
 */
public class UserStatusFixture {

  /**
   * 유효한 UserStatus를 생성한다 (기본 온라인 상태)
   */
  public static UserStatus createValidUserStatus(UUID userId) {
    return UserStatus.create(userId);
  }

  /**
   * 오프라인 상태의 UserStatus를 생성한다. lastActiveAt을 현재 시간 기준 6분 전으로 설정한다.
   */
  public static UserStatus createOfflineUserStatus(UUID userId) {
    UserStatus userStatus = UserStatus.create(userId);
    try {
      Field lastActiveAtField = UserStatus.class.getDeclaredField("lastActiveAt");
      lastActiveAtField.setAccessible(true);
      lastActiveAtField.set(userStatus, Instant.now().minus(Duration.ofMinutes(6)));
    } catch (NoSuchFieldException | IllegalAccessException e) {
      throw new IllegalStateException("리플렉션을 통한 lastActiveAt 설정 실패", e);
    }
    return userStatus;
  }

  /**
   * 특정 시간만큼 lastActiveAt을 조정한 UserStatus를 생성한다. 예: duration = 10분 -> 현재 시간에서 10분 전으로 lastActiveAt
   * 설정
   */
  public static UserStatus createUserStatusWithCustomLastActiveAt(UUID userId,
      Duration durationBeforeNow) {
    UserStatus userStatus = UserStatus.create(userId);
    try {
      Field lastActiveAtField = UserStatus.class.getDeclaredField("lastActiveAt");
      lastActiveAtField.setAccessible(true);
      lastActiveAtField.set(userStatus, Instant.now().minus(durationBeforeNow));
    } catch (NoSuchFieldException | IllegalAccessException e) {
      throw new IllegalStateException("리플렉션을 통한 lastActiveAt 설정 실패", e);
    }
    return userStatus;
  }
}
