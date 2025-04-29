package com.sprint.mission.discodeit.fixture;

import com.sprint.mission.discodeit.entity.UserStatus;
import java.lang.reflect.Field;
import java.time.Instant;
import java.util.UUID;

public class UserStatusFixture {

  public static UserStatus createValidUserStatus(UUID userId) {
    return UserStatus.create(userId);
  }

  public static UserStatus createValidUserStatusOffline(UUID userId) {
    UserStatus userStatus = UserStatus.create(userId);
    try {
      Field lastActiveAtField = UserStatus.class.getDeclaredField("lastActiveAt");
      lastActiveAtField.setAccessible(true);
      lastActiveAtField.set(userStatus, Instant.now().minusMillis(6)); // 현재 시간보다 6분 이전으로 설정
    } catch (NoSuchFieldException | IllegalAccessException e) {
      // 리플렉션 실패 시 예외 처리 (테스트 환경에서는 심각한 오류이므로 RuntimeException으로 wrapping)
      throw new RuntimeException("리플렉션을 통한 lastActiveAt 설정 실패", e);
    }
    return userStatus;
  }
}