package com.sprint.mission.discodeit.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.sprint.mission.discodeit.fixture.UserFixture;
import java.util.UUID;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class UserTest {

  @Nested
  class 사용자_생성 {

    @Test
    void 기본_정보로_사용자_생성_시_필수_필드가_설정된다() {
      User user = UserFixture.createValidUser();

      assertAll(
          () -> assertThat(user.getId()).isNotNull(),
          () -> assertThat(user.getEmail()).isNotNull(),
          () -> assertThat(user.getName()).isNotNull(),
          () -> assertThat(user.getPassword()).isNotNull(),
          () -> assertThat(user.getCreatedAt()).isNotNull(),
          () -> assertThat(user.getProfileId()).isNull()
      );
    }

    @Test
    void 프로필_이미지를_포함하여_사용자를_생성할_수_있다() {
      UUID profileId = UUID.randomUUID();
      User user = User.create("test@example.com", "tester", "pass123", profileId);

      assertThat(user.getProfileId()).isEqualTo(profileId);
    }
  }

  @Nested
  class 사용자_정보_수정 {

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
