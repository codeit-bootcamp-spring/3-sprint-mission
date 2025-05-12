package com.sprint.mission.discodeit.fixture;

import com.sprint.mission.discodeit.entity.UserStatus;
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
   * 오프라인 상태의 UserStatus를 생성한다.
   */
  public static UserStatus createOfflineUserStatus(UUID userId) {
    UserStatus userStatus = UserStatus.create(userId);
    userStatus.updateLastActiveAt(Instant.now().minus(Duration.ofMinutes(6)));
    return userStatus;
  }

  /**
   * lastActiveAt을 커스텀하게 설정한다.
   */
  public static UserStatus createUserStatusWithCustomLastActiveAt(UUID userId,
      Duration durationBeforeNow) {
    UserStatus userStatus = UserStatus.create(userId);
    userStatus.updateLastActiveAt(Instant.now().minus(durationBeforeNow));
    return userStatus;
  }
}
