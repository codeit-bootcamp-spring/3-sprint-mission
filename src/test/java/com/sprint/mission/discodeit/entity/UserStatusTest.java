package com.sprint.mission.discodeit.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.sprint.mission.discodeit.fixture.UserStatusFixture;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class UserStatusTest {

  @Nested
  class Create {

    @Test
    void 유저_생성_시_유저_상태가_올바르게_생성되어야_한다() {
      UserStatus userStatus = UserStatusFixture.createValidUserStatus(UUID.randomUUID());

      assertAll(
          () -> assertThat(userStatus).isNotNull(),
          () -> assertThat(userStatus.getUserId()).isNotNull(),
          () -> assertThat(userStatus.getLastActiveAt()).isNotNull(),
          () -> assertThat(userStatus.getId()).isNotNull(),
          () -> assertThat(userStatus.getCreatedAt()).isNotNull(),
          () -> assertThat(userStatus.getUpdatedAt()).isEqualTo(userStatus.getCreatedAt()),
          () -> assertThat(userStatus.isOnline()).isTrue()
      );
    }
  }

  @Nested
  class Update {

    @Test
    void 마지막_활동_시간_업데이트시_시간과_수정_시간이_갱신되어야_한다() {
      UserStatus userStatus = UserStatusFixture.createValidUserStatus(UUID.randomUUID());
      Instant originalUpdatedAt = userStatus.getUpdatedAt();
      Instant originalLastActiveAt = userStatus.getLastActiveAt();

      userStatus.updateLastActiveAt();

      assertAll(
          () -> assertThat(userStatus.getLastActiveAt()).isAfterOrEqualTo(originalLastActiveAt),
          () -> assertThat(userStatus.getUpdatedAt()).isAfterOrEqualTo(originalUpdatedAt)
      );
    }

    @Test
    void 사용자는_활성_상태여야_한다() {
      UserStatus userStatus = UserStatusFixture.createValidUserStatus(UUID.randomUUID());

      assertThat(userStatus.isOnline()).isTrue();
    }

    @Test
    void 사용자는_비활성_상태여야_한다() {
      UserStatus userStatus = UserStatusFixture.createOfflineUserStatus(UUID.randomUUID());

      assertThat(userStatus.isOnline()).isFalse();
    }
  }

  @Nested
  class Read {

    @Test
    void 동일한_속성을_가져도_고유한_아이디를_가진다() {
      UUID userId = UUID.randomUUID();
      UserStatus userStatus1 = UserStatusFixture.createValidUserStatus(userId);
      UserStatus userStatus2 = UserStatusFixture.createValidUserStatus(userId);

      assertAll(
          () -> assertThat(userStatus1).isNotEqualTo(userStatus2),
          () -> assertThat(userStatus1.hashCode()).isNotEqualTo(userStatus2.hashCode())
      );
    }

    @Test
    void 서로_다른_속성을_가진_두_유저_상태_객체는_equals_비교시_false를_반환해야_한다() {
      UserStatus userStatus1 = UserStatusFixture.createValidUserStatus(UUID.randomUUID());
      UserStatus userStatus2 = UserStatusFixture.createValidUserStatus(UUID.randomUUID());

      assertAll(
          () -> assertThat(userStatus1.equals(userStatus2)).isFalse(),
          () -> assertThat(userStatus1.hashCode()).isNotEqualTo(userStatus2.hashCode())
      );
    }

    @Test
    void 동일_객체_equals_비교시_true를_반환해야_한다() {
      UserStatus userStatus = UserStatusFixture.createValidUserStatus(UUID.randomUUID());

      assertThat(userStatus.equals(userStatus)).isTrue();
    }

    @Test
    void toString_메서드는_유저_상태의_모든_필드를_포함해야_한다() {
      UserStatus userStatus = UserStatusFixture.createValidUserStatus(UUID.randomUUID());

      String toString = userStatus.toString();

      assertAll(
          () -> assertThat(toString).contains(userStatus.getId().toString()),
          () -> assertThat(toString).contains(userStatus.getUserId().toString()),
          () -> assertThat(toString).contains("createdAt"),
          () -> assertThat(toString).contains("updatedAt"),
          () -> assertThat(toString).contains("lastActiveAt")
      );
    }
  }
}
