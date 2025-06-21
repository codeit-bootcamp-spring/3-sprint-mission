package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.fixture.BinaryContentFixture;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DataJpaTest
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

  @Test
  void 사용자_이메일로_조회_성공() {
    // given
    String email = "findby@email.com";
    String name = "테스터";
    String password = "pw1234";
    BinaryContent profile = BinaryContentFixture.createValid();
    User user = User.create(email, name, password, profile);
    userRepository.save(user);
    // when
    var result = userRepository.findByEmail(email);
    // then
    assertThat(result).isPresent();
    assertThat(result.get().getEmail()).isEqualTo(email);
  }

  @Test
  void 사용자_이메일로_조회_실패() {
    // when
    var result = userRepository.findByEmail("notfound@email.com");
    // then
    assertThat(result).isNotPresent();
  }

  @Test
  void 사용자_이름으로_조회_성공() {
    // given
    String email = "findby2@email.com";
    String name = "홍길동";
    String password = "pw1234";
    BinaryContent profile = BinaryContentFixture.createValid();
    User user = User.create(email, name, password, profile);
    userRepository.save(user);
    // when
    var result = userRepository.findByUsername(name);
    // then
    assertThat(result).isPresent();
    assertThat(result.get().getUsername()).isEqualTo(name);
  }

  @Test
  void 사용자_이름으로_조회_실패() {
    // when
    var result = userRepository.findByUsername("없는이름");
    // then
    assertThat(result).isNotPresent();
  }
}
