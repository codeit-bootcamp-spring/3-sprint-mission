package com.sprint.mission.discodeit.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.sprint.mission.discodeit.fixture.UserFixture;
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
      // given
      User user = UserFixture.createValidUser();

      // when
      UserStatus userStatus = UserStatus.create(user.getId());

      // then
      assertAll(
          "유저 상태 기본 정보 검증",
          () -> assertThat(userStatus).isNotNull(),
          () -> assertThat(userStatus.getUserId()).isEqualTo(user.getId()),
          () -> assertThat(userStatus.getLastActiveAt()).isNotNull(),
          () -> assertThat(userStatus.getId()).isNotNull(),
          () -> assertThat(userStatus.getCreatedAt()).isNotNull(),
          () -> assertThat(userStatus.getUpdatedAt()).isNotNull(),
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
      // given
      User user = UserFixture.createValidUser();
      UserStatus userStatus = UserStatus.create(user.getId());
      Instant originalUpdatedAt = userStatus.getUpdatedAt();
      Instant originalLastActiveAt = userStatus.getLastActiveAt();

      // when
      // 약간의 지연을 통해 시간 변화 확보
      try {
        Thread.sleep(10);
      } catch (InterruptedException e) { /* 무시 */ }
      userStatus.updateLastActiveAt();

      // then
      assertAll(
          "마지막 활동 시간 업데이트 검증",
          () -> assertThat(userStatus.getLastActiveAt()).as("마지막 활동 시간이 갱신되어야 함")
              .isAfter(originalLastActiveAt),
          () -> assertThat(userStatus.getUpdatedAt()).as("수정 시간이 갱신되어야 함")
              .isAfter(originalUpdatedAt)
      );
    }

    @Test
    @DisplayName("5분 미만 경과한 사용자는 활성 상태여야 한다")
    void shouldBeActiveWithinFiveMinutes() {
      // given
      UUID userId = UUID.randomUUID();

      // UserStatus 생성 로직 확인: 객체 생성 및 필드 설정
      UserStatus userStatus = UserStatusFixture.createValidUserStatus(userId);

      // when & then
      assertThat(userStatus.isCurrentlyActive()).as("4분 전 활동한 사용자는 활성 상태여야 함")
          .isTrue();
    }

    @Test
    @DisplayName("5분 이상 경과한 사용자는 비활성 상태여야 한다")
    void shouldBeInactiveAfterFiveMinutes() {
      // given
      UUID userId = UUID.randomUUID();

      // UserStatus 생성 로직 확인: 객체 생성 및 필드 설정
      UserStatus userStatus = UserStatusFixture.createValidUserStatusOffline(userId);

      System.out.println(userStatus.isCurrentlyActive());

      // when & then
      assertThat(userStatus.isCurrentlyActive()).as("6분 전 활동한 사용자는 비활성 상태여야 함")
          .isFalse();
    }
  }

  @Nested
  @DisplayName("객체 동등성 및 해시코드")
  class EqualsAndHashCode {

    @Test
    @DisplayName("동일한 속성을 가져도 고유한 아이디를 가진다.")
    void equalsSameProperties() {
      // given
      UUID userId = UUID.randomUUID();

      UserStatus userStatus1 = UserStatusFixture.createValidUserStatus(userId);
      UserStatus userStatus2 = UserStatusFixture.createValidUserStatus(userId);

      System.out.println();

      // when & then
      assertThat(userStatus1).as("동일한 속성을 가져도 고유한 아이디로 각자 다른 객체").isNotEqualTo(userStatus2);
      assertThat(userStatus1.hashCode()).as("동일한 속성을 가져도 다른 해시코드를 가짐")
          .isNotEqualTo(userStatus2.hashCode());
    }

    @Test
    @DisplayName("서로 다른 속성을 가진 두 유저 상태 객체는 equals 비교시 false를 반환해야 한다")
    void equalsDifferentProperties() {
      // given
      UUID id1 = UUID.randomUUID();
      UUID id2 = UUID.randomUUID();

      UserStatus userStatus1 = UserStatusFixture.createValidUserStatus(id1);
      UserStatus userStatus2 = UserStatusFixture.createValidUserStatus(id2);

      // when & then
      assertThat(userStatus1.equals(userStatus2)).as("서로 다른 속성을 가진 객체는 equals로 비교시 false")
          .isFalse();
      assertThat(userStatus1.hashCode()).as("서로 다른 속성을 가진 객체는 다른 해시코드를 가짐")
          .isNotEqualTo(userStatus2.hashCode());
    }

    @Test
    @DisplayName("동일 객체 equals 비교시 true를 반환해야 한다")
    void equalsSameObject() {
      // given
      UserStatus userStatus = UserStatus.create(UUID.randomUUID());

      // when & then
      assertThat(userStatus.equals(userStatus)).as("동일 객체는 equals로 비교시 true").isTrue();
    }
  }

  @Nested
  @DisplayName("문자열 표현")
  class ToStringTest {

    @Test
    @DisplayName("toString 메서드는 유저 상태의 모든 필드를 포함해야 한다")
    void toStringShouldContainAllFields() {
      // given
      UUID userId = UUID.randomUUID();
      UserStatus userStatus = UserStatus.create(userId);

      // when
      String toString = userStatus.toString();

      // then
      assertAll(
          "toString 필드 포함 검증",
          () -> assertThat(toString).contains(userStatus.getId().toString()),
          () -> assertThat(toString).contains(userId.toString()),
          () -> assertThat(toString).contains("createdAt"),
          () -> assertThat(toString).contains("updatedAt"),
          () -> assertThat(toString).contains("lastActiveAt")
      );
    }
  }

}