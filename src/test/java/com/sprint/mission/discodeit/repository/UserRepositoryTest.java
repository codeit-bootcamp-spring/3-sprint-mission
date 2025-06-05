package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.config.JpaAuditingConfig;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.fixture.BinaryContentFixture;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DataJpaTest
@Import(JpaAuditingConfig.class)
class UserRepositoryTest {

  @Autowired
  TestEntityManager em;

  @Autowired
  private UserRepository userRepository;

  @Test
  void 사용자를_저장하고_조회할_수_있다() {
    // given
    String email = "test@test.com";
    String name = "길동쓰";
    String password = "pwd123";

    BinaryContent profile = BinaryContentFixture.createValid();
    User user = User.create(email, name, password, profile);
    user.updateProfile(profile);

    // when
    userRepository.save(user);
    Optional<User> result = userRepository.findById(user.getId());

    // then
    assertThat(result).isPresent();
    assertThat(result.get().getEmail()).isEqualTo(email);
    assertThat(result.get().getUsername()).isEqualTo(name);
    assertThat(result.get().getPassword()).isEqualTo(password);
    assertThat(result.get().getProfile()).isEqualTo(profile);
  }
}
