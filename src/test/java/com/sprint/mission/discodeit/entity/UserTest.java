package com.sprint.mission.discodeit.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.sprint.mission.discodeit.fixture.UserFixture;
import java.util.UUID;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class UserTest {

  @Nested
  class Create {

    @Test
    void 기본_정보로_사용자_생성_시_필수_필드가_설정된다() {
      User expected = User.create("test@example.com", "홍길동", "password123");
      User actual = User.create("test@example.com", "홍길동", "password123");

      assertThat(actual)
          .usingRecursiveComparison()
          .ignoringFields("id", "createdAt", "updatedAt")
          .isEqualTo(expected);
    }

    @Test
    void 프로필_이미지를_포함하여_사용자를_생성할_수_있다() {
      UUID profileId = UUID.randomUUID();
      User user = User.create("test@example.com", "tester", "pass123", profileId);

      assertThat(user.getProfileId()).isEqualTo(profileId);
    }
  }

  @Nested
  class Update {

    @Test
    void 이름을_수정하면_이름과_수정시간이_업데이트된다() {
      User user = UserFixture.createValidUser();
      String newName = "새이름";

      user.updateName(newName);

      assertAll(
          () -> assertThat(user.getName()).isEqualTo(newName),
          () -> assertThat(user.getUpdatedAt()).isNotNull()
      );
    }

    @Test
    void 이메일을_수정하면_이메일과_수정시간이_업데이트된다() {
      User user = UserFixture.createValidUser();
      String newEmail = "updated@test.com";

      user.updateEmail(newEmail);

      assertAll(
          () -> assertThat(user.getEmail()).isEqualTo(newEmail),
          () -> assertThat(user.getUpdatedAt()).isNotNull()
      );
    }

    @Test
    void 비밀번호를_수정하면_비밀번호와_수정시간이_업데이트된다() {
      User user = UserFixture.createValidUser();
      String newPassword = "newpass123";

      user.updatePassword(newPassword);

      assertAll(
          () -> assertThat(user.getPassword()).isEqualTo(newPassword),
          () -> assertThat(user.getUpdatedAt()).isNotNull()
      );
    }

    @Test
    void 프로필_ID를_수정하면_ID와_수정시간이_업데이트된다() {
      User user = UserFixture.createValidUser();
      UUID newProfileId = UUID.randomUUID();

      user.updateProfileId(newProfileId);

      assertAll(
          () -> assertThat(user.getProfileId()).isEqualTo(newProfileId),
          () -> assertThat(user.getUpdatedAt()).isNotNull()
      );
    }
  }
}
