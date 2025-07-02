package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.fixture.BinaryContentFixture;
import com.sprint.mission.discodeit.fixture.UserFixture;
import com.sprint.mission.discodeit.fixture.UserStatusFixture;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DataJpaTest

public class UserStatusRepositoryTest {

  @Autowired
  TestEntityManager em;

  @Autowired
  private UserStatusRepository userStatusRepository;

  @Test
  void 사용자로_사용자_상태를_조회할_수_있다() {
    // given
    User user = UserFixture.createValidUser(BinaryContentFixture.createValid());
    UserStatus status = UserStatusFixture.createValid(user);
    user.updateUserStatus(status);
    em.persist(user);

    // when
    Optional<UserStatus> result = userStatusRepository.findByUser(user);

    // then
    assertThat(result).isPresent();
    assertThat(result.get().getUser()).isEqualTo(user);
  }
}
