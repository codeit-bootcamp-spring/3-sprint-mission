package com.sprint.mission.discodeit.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.sprint.mission.discodeit.fixture.UserStatusFixture;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("UserStatus 엔티티 테스트")
public class UserStatusTest {

  @Nested
  @DisplayName("유저 상태 생성")
  class Create {

    @Test
    @DisplayName("유저 생성 시 유저 상태가 올바르게 생성되어야 한다")
    void shouldCreateUserStatus() {
      UserStatus userStatus = UserStatusFixture.createValidUserStatus(UUID.randomUUID());

      assertAll(
          () -> assertThat(userStatus).isNotNull(),
          () -> assertThat(userStatus.getUserId()).isNotNull(),
          () -> assertThat(userStatus.getLastActiveAt()).isNotNull(),
          () -> assertThat(userStatus.getId()).isNotNull(),
          () -> assertThat(userStatus.getCreatedAt()).isNotNull(),
          () -> assertThat(userStatus.getUpdatedAt()).isEqualTo(userStatus.getCreatedAt()),
          () -> assertThat(userStatus.isCurrentlyActive()).isTrue()
      );
    }
  }

  @Nested
  @DisplayName("유저 상태 업데이트")
  class Update {

    @Test
    @DisplayName("마지막 활동 시간 업데이트시 시간과 수정 시간이 갱신되어야 한다")
    void shouldUpdateLastActiveAt() {
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
    @DisplayName("5분 미만 경과한 사용자는 활성 상태여야 한다")
    void shouldBeActiveWithinFiveMinutes() {
      UserStatus userStatus = UserStatusFixture.createValidUserStatus(UUID.randomUUID());

      assertThat(userStatus.isCurrentlyActive()).isTrue();
    }

    @Test
    @DisplayName("5분 이상 경과한 사용자는 비활성 상태여야 한다")
    void shouldBeInactiveAfterFiveMinutes() {
      UserStatus userStatus = UserStatusFixture.createOfflineUserStatus(UUID.randomUUID());

      assertThat(userStatus.isCurrentlyActive()).isFalse();
    }
  }

  @Nested
  @DisplayName("객체 동등성 및 해시코드")
  class EqualsAndHashCode {

    @Test
    @DisplayName("동일한 속성을 가져도 고유한 아이디를 가진다")
    void equalsSameProperties() {
      UUID userId = UUID.randomUUID();
      UserStatus userStatus1 = UserStatusFixture.createValidUserStatus(userId);
      UserStatus userStatus2 = UserStatusFixture.createValidUserStatus(userId);

      assertAll(
          () -> assertThat(userStatus1).isNotEqualTo(userStatus2),
          () -> assertThat(userStatus1.hashCode()).isNotEqualTo(userStatus2.hashCode())
      );
    }

    @Test
    @DisplayName("서로 다른 속성을 가진 두 유저 상태 객체는 equals 비교시 false를 반환해야 한다")
    void equalsDifferentProperties() {
      UserStatus userStatus1 = UserStatusFixture.createValidUserStatus(UUID.randomUUID());
      UserStatus userStatus2 = UserStatusFixture.createValidUserStatus(UUID.randomUUID());

      assertAll(
          () -> assertThat(userStatus1.equals(userStatus2)).isFalse(),
          () -> assertThat(userStatus1.hashCode()).isNotEqualTo(userStatus2.hashCode())
      );
    }

    @Test
    @DisplayName("동일 객체 equals 비교시 true를 반환해야 한다")
    void equalsSameObject() {
      UserStatus userStatus = UserStatusFixture.createValidUserStatus(UUID.randomUUID());

      assertThat(userStatus.equals(userStatus)).isTrue();
    }
  }

  @Nested
  @DisplayName("문자열 표현")
  class ToStringTest {

    @Test
    @DisplayName("toString 메서드는 유저 상태의 모든 필드를 포함해야 한다")
    void toStringShouldContainAllFields() {
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
