package com.sprint.mission.discodeit.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.fixture.UserStatusFixture;
import java.util.UUID;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class UserStatusTest {

  private UserStatus createValidUserStatus(UUID userId) {
    return UserStatusFixture.createValidUserStatus(userId);
  }

  private UserStatus createOfflineUserStatus(UUID userId) {
    return UserStatusFixture.createOfflineUserStatus(userId);
  }

  @Nested
  class Create {

    @Test
    void 유저_생성_시_온라인_상태로_시작해야_한다() {
      UUID userId = UUID.randomUUID();
      UserStatus userStatus = createValidUserStatus(userId);

      assertThat(userStatus.isOnline()).isTrue();
    }
  }

  @Nested
  class Update {

    @Test
    void 오프라인_유저가_업데이트시_온라인_상태로_변경되어야_한다() {
      UUID userId = UUID.randomUUID();
      UserStatus userStatus = createOfflineUserStatus(userId);

      assertThat(userStatus.isOnline()).isFalse();

      userStatus.updateLastActiveAt();

      assertThat(userStatus.isOnline()).isTrue();
    }
  }
}
