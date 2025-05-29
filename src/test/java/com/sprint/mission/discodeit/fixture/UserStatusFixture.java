package com.sprint.mission.discodeit.fixture;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public class UserStatusFixture {

  public static UserStatus createValid(User user) {
    return UserStatus.create(user);
  }

  public static UserStatus createWithId(User user) {
    UserStatus userStatus = UserStatus.create(user);
    userStatus.assignIdForTest(UUID.randomUUID());
    return userStatus;
  }

  public static UserStatus createOffline(User user) {
    UserStatus userStatus = UserStatus.create(user);
    userStatus.updateLastActiveAt(Instant.now().minus(Duration.ofMinutes(6)));
    return userStatus;
  }
}
