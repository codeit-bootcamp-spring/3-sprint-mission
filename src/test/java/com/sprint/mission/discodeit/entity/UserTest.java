package com.sprint.mission.discodeit.entity;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.sprint.mission.discodeit.fixture.UserFixture;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class UserTest {

  @Nested
  @DisplayName("유저 생성")
  class Create {

    @Test
    @DisplayName("유저가 생성되면 기본 정보가 올바르게 설정되어야 한다")
    void shouldCreateUserWithDefaultInfo() {
      // given
      String email = "test@test.com";
      String name = "길동쓰";
      String password = "pwd123";

      // when
      User user = UserFixture.createCustomUser(email, name, password);

      // then
      assertAll(
          "유저 기본 정보 검증",
          () -> assertThat(user.getId()).as("유저 ID는 null이 아니어야 함").isNotNull(),
          () -> assertThat(user.getEmail()).as("이메일이 올바르게 설정되어야 함").isEqualTo(email),
          () -> assertThat(user.getName()).as("이름이 올바르게 설정되어야 함").isEqualTo(name),
          () -> assertThat(user.getPassword()).as("비밀번호가 올바르게 설정되어야 함").isEqualTo(password),
          () -> assertThat(user.getCreatedAt()).as("생성 시간이 올바르게 설정되어야 함").isNotNull()
      );
    }
  }

  @Nested
  @DisplayName("유저 정보 수정")
  class Update {

    @Test
    @DisplayName("유저 정보 수정 시 수정 정보와 시간이 업데이트되어야 한다")
    void shouldUpdateNameAndTimestamp() {
      // given
      User user = UserFixture.createValidUser();
      String newName = "새로운 유저명";
      String newPassword = "newpass123";
      Instant originalUpdatedAt = user.getUpdatedAt();

      System.out.println(originalUpdatedAt);

      // when & then
      assertAll(
          "유저 정보 수정 검증",
          () -> {
            user.updateName(newName);

            assertAll(
                "이름 변경 검증",
                () -> assertThat(user.getName()).as("새로운 이름으로 변경되어야 함").isEqualTo(newName),
                () -> assertThat(user.getUpdatedAt()).as("이름 변경 시 수정 시간이 갱신되어야 함")
                    .isAfterOrEqualTo(originalUpdatedAt)
            );
          },
          () -> {
            user.updatePassword(newPassword);

            assertAll(
                "비밀번호 변경 검증",
                () -> assertThat(user.getPassword()).as("새로운 비밀번호로 변경되어야 함").isEqualTo(newPassword),
                () -> assertThat(user.getUpdatedAt()).as("비밀번호 변경 시 시간이 갱신되어야 함")
                    .isAfterOrEqualTo(originalUpdatedAt)
            );
          }
      );
    }
  }
}