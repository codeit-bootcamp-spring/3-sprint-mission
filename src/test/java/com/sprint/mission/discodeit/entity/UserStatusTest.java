package com.sprint.mission.discodeit.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.sprint.mission.discodeit.fixture.UserFixture;
import java.lang.reflect.Field;
import java.time.Duration;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class UserStatusTest {

  @Nested
  @DisplayName("유저 상태 생성")
  class Create {

    @Test
    @DisplayName("유저 생성 시 유저 상태가 올바르게 생성되어야 한다")
    void shouldCreateUserStatus() {
      // given
      User user = UserFixture.createDefaultUser();

      // when
      UserStatus userStatus = UserStatus.create(user.getId());

      // then
      assertAll(
          "유저 상태 기본 정보 검증",
          () -> assertThat(userStatus).isNotNull(),
          () -> assertThat(userStatus.getUserId()).isEqualTo(user.getId()),
          () -> assertThat(userStatus.getLastActiveAt()).isNotNull(),
          () -> assertThat(userStatus.isCurrentlyActive()).isTrue()
      );
    }
  }

  @Nested
  @DisplayName("유저 상태 업데이트")
  class Update {

    @Test
    @DisplayName("접속 후 5분이 경과한 유저 상태는 로그아웃이어야 한다")
    void shouldModifyLastActiveAtUsingReflection()
        throws NoSuchFieldException, IllegalAccessException {
      // given
      User user = UserFixture.createDefaultUser();
      UserStatus userStatus = UserStatus.create(user.getId());

      // when
      // 리플렉션을 사용하여 'lastActiveAt' 필드에 접근하여 값을 변경
      Field lastActiveAtField = UserStatus.class.getDeclaredField("lastActiveAt");
      lastActiveAtField.setAccessible(true); // private 필드에 접근 가능하도록 설정

      // 6분 전으로 lastActiveAt 값을 설정
      lastActiveAtField.set(userStatus, Instant.now().minus(Duration.ofMinutes(6)));

      // then
      assertThat(userStatus.isCurrentlyActive()).isFalse();
    }
  }
}
