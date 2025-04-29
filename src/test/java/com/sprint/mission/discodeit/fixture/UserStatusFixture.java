package com.sprint.mission.discodeit.fixture;

import com.sprint.mission.discodeit.entity.UserStatus;
import java.lang.reflect.Field;
import java.time.Duration;
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
      // 6분 전으로 설정 (이전에는 6밀리초를 빼고 있었음)
      lastActiveAtField.set(userStatus, Instant.now().minus(Duration.ofMinutes(6)));
      lastActiveAtField.setAccessible(false); // 접근 권한 다시 닫기
    } catch (NoSuchFieldException | IllegalAccessException e) {
      // 리플렉션 실패 시 예외 처리 (테스트 환경에서는 심각한 오류이므로 RuntimeException으로 wrapping)
      throw new RuntimeException("리플렉션을 통한 lastActiveAt 설정 실패", e);
    }
    return userStatus;
  }
}