package com.sprint.mission.discodeit.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.fixture.UserFixture;
import java.time.Duration;
import java.time.Instant;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DataJpaTest
class UserStatusTest {

  @Autowired
  TestEntityManager em;

  @Nested
  class Create {

    @Test
    void 유저_생성_시_온라인_상태로_시작해야_한다() {
      User user = UserFixture.createValidUser();
      UserStatus userStatus = UserStatus.create(user);

      em.persist(user);
      em.persist(userStatus);
      em.flush();
      em.clear();

      assertThat(userStatus.isOnline()).isTrue();
    }

    @Test
    void 유저_저장시_상태정보도_함께_저장되어야_한다() {
      User user = UserFixture.createValidUser();
      UserStatus status = UserStatus.create(user);
      user.updateUserStatus(status);

      em.persist(user);
      em.flush();
      em.clear();

      User found = em.find(User.class, user.getId());
      assertThat(found.getUserStatus()).isNotNull();
    }
  }

  @Nested
  class Update {

    @Test
    void 오프라인_유저가_업데이트시_온라인_상태로_변경되어야_한다() {
      User user = UserFixture.createValidUser();
      UserStatus userStatus = UserStatus.create(user);

      user.updateUserStatus(userStatus);
      em.persist(user);
      em.flush();
      em.clear();

      userStatus.updateLastActiveAt(Instant.now().minus(Duration.ofMinutes(10)));

      assertThat(userStatus.isOnline()).isFalse();

      userStatus.updateLastActiveAt();

      assertThat(userStatus.isOnline()).isTrue();
    }
  }
}
