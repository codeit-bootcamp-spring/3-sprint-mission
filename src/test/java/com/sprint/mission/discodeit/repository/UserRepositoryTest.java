package com.sprint.mission.discodeit.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@EnableJpaAuditing
@DataJpaTest
@DisplayName("UserRepository 슬라이스 테스트")
public class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  @Test
  @DisplayName("모든 사용자 리스트를 프로필, 유저 status 와 함께 가져오기")
  void findAllWithProfileAndStatus_thenReturnUserWithProfileAndStatus() {
    // Given
//    INSERT INTO users (id, created_at, username, email, password, profile_id)
//    VALUES ('00000000-0000-0000-0000-000000000010', now(), 'admin', 'admin@example.com',
//        'hashedpassword1', '00000000-0000-0000-0000-000000000001'),
//        ('00000000-0000-0000-0000-000000000011', now(), 'user2', 'user2@example.com',
//        'hashedpassword2', null);

    // When
    List<User> users = userRepository.findAllWithProfileAndStatus();

    // Then

    assertEquals(2, users.size());
    assertNotNull(users.get(0).getProfile().getFileName());
    assertNotNull(users.get(1).getStatus().getLastActiveAt());
  }

  @Test
  @DisplayName("사용자 프로필 정보와 함께 가져오기")
  void whenFindWithUserName_thenReturnUserWithProfile() {
    // Given
//    INSERT INTO users (id, created_at, username, email, password, profile_id)
//    VALUES ('00000000-0000-0000-0000-000000000010', now(), 'admin', 'admin@example.com',
//        'hashedpassword1', '00000000-0000-0000-0000-000000000001'),
//        ('00000000-0000-0000-0000-000000000011', now(), 'user2', 'user2@example.com',
//        'hashedpassword2', null);

    // When
    Optional<User> user = userRepository.findByUsername("admin");

    // Then

    assertNotNull(user);
    assertNotNull(user.get().getProfile().getFileName());
  }

  @DisplayName("존재하지 않는 사용자명을 조회하면 Optional.empty 반환")
  @Test
  void whenFindWithInvalidUsername_thenReturnEmptyOptional() {
    // Given
    String invalidUsername = "not_exist_user";

    // When
    Optional<User> result = userRepository.findByUsername(invalidUsername);

    // Then
    assertTrue(result.isEmpty(), "존재하지 않는 사용자명일 경우 Optional.empty() 반환해야 함");
  }

}
