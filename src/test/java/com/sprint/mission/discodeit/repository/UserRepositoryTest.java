package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.config.JpaAuditingConfig;
import com.sprint.mission.discodeit.entity.User;
import java.util.Optional;
import java.util.UUID;
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
  private JpaUserRepository userRepository;

  @Test
  void 사용자를_저장하고_조회할_수_있다() {
    // given
    UUID profileId = UUID.randomUUID();
    User user = User.create("test@example.com", "테스트 유저", "password123!", profileId);

    // when
    User saved = userRepository.save(user);
    em.flush();

    Optional<User> result = userRepository.findById(saved.getId());

    // then
    assertThat(result).isPresent();
    assertThat(result.get().getEmail()).isEqualTo("test@example.com");
    assertThat(result.get().getUsername()).isEqualTo("테스트 유저");
    assertThat(result.get().getPassword()).isEqualTo("password123!");
    assertThat(result.get().getProfileId()).isEqualTo(profileId);
  }
}
