package com.sprint.mission.discodeit.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.sprint.mission.discodeit.config.JpaAuditingConfig;
import com.sprint.mission.discodeit.fixture.BinaryContentFixture;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DataJpaTest
@Import(JpaAuditingConfig.class)
class UserTest {

  @Autowired
  TestEntityManager em;

  @Nested
  class Create {

    @Test
    void 기본_정보로_사용자_생성_시_ID와_CreatedAt이_자동_설정된다() {
      User user = User.create("test@example.com", "홍길동", "password123", null);

      em.persist(user);
      em.flush();
      em.clear();

      User found = em.find(User.class, user.getId());

      assertThat(found.getId()).isNotNull();
      assertThat(found.getCreatedAt()).isNotNull();
    }

    @Test
    void 프로필_이미지를_포함하여_사용자를_생성할_수_있다() {
      User user = User.create("test@example.com", "tester", "pass123",
          BinaryContentFixture.createValid());

      em.persist(user);
      em.flush();
      em.clear();

      User found = em.find(User.class, user.getId());
      assertThat(found.getProfile()).isNotNull();
    }
  }

  @Nested
  class Update {

    @Test
    void 이름을_수정하면_업데이트시간도_갱신된다() {
      User user = User.create("test@example.com", "홍길동", "password123", null);

      em.persist(user);
      em.flush();
      em.clear();

      User managedUser = em.find(User.class, user.getId());
      managedUser.updateName("새이름");
      em.flush();
      em.clear();

      User updated = em.find(User.class, user.getId());

      assertAll(
          () -> assertThat(updated.getUsername()).isEqualTo("새이름"),
          () -> assertThat(updated.getUpdatedAt()).isNotNull()
      );
    }

    @Test
    void 이메일을_수정하면_업데이트시간도_갱신된다() {
      User user = User.create("test@example.com", "홍길동", "password123", null);

      em.persist(user);
      em.flush();
      em.clear();

      User managedUser = em.find(User.class, user.getId());
      managedUser.updateEmail("updated@example.com");
      em.flush();
      em.clear();

      User updated = em.find(User.class, user.getId());

      assertAll(
          () -> assertThat(updated.getEmail()).isEqualTo("updated@example.com"),
          () -> assertThat(updated.getUpdatedAt()).isNotNull()
      );
    }

    @Test
    void 비밀번호_수정시_업데이트시간도_갱신된다() {
      User user = User.create("test@example.com", "홍길동", "password123", null);

      em.persist(user);
      em.flush();
      em.clear();

      User managedUser = em.find(User.class, user.getId());
      managedUser.updatePassword("newpass");
      em.flush();
      em.clear();

      User updated = em.find(User.class, user.getId());

      assertAll(
          () -> assertThat(updated.getPassword()).isEqualTo("newpass"),
          () -> assertThat(updated.getUpdatedAt()).isNotNull()
      );
    }

    @Test
    void 프로필_변경시_업데이트시간도_갱신된다() {
      User user = User.create("test@example.com", "홍길동", "password123", null);

      em.persist(user);
      em.flush();
      em.clear();

      User managedUser = em.find(User.class, user.getId());
      managedUser.updateProfile(BinaryContentFixture.createValid());
      em.flush();
      em.clear();

      User updated = em.find(User.class, user.getId());

      assertAll(
          () -> assertThat(updated.getProfile()).isNotNull(),
          () -> assertThat(updated.getUpdatedAt()).isNotNull()
      );
    }
  }
}
