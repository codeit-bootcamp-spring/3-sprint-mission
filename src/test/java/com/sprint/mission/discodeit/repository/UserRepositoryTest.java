package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    private User user;

    @BeforeEach
    void setup() {
        BinaryContent profile = new BinaryContent("profile.png", 1024L, "image/png");
        user = new User("testuser", "test@email.com", "password", null, Role.USER);
        userRepository.save(user);
    }

    @Test
    @DisplayName("사용자 조회 성공 - username 기준")
    void shouldFindUserByUsernameSuccessfully() {
        Optional<User> result = userRepository.findByUsername("testuser");

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("test@email.com");
    }

    @Test
    @DisplayName("사용자 조회 실패 - username 기준")
    void shouldReturnEmpty_whenUserNotFoundByUsername() {
        Optional<User> result = userRepository.findByUsername("wronguser");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("이메일 존재 여부 확인 - 존재함")
    void shouldReturnTrue_whenEmailExists() {
        boolean exists = userRepository.existsByEmail("test@email.com");

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("이메일 존재 여부 확인 - 존재하지 않음")
    void shouldReturnFalse_whenEmailDoesNotExist() {
        boolean exists = userRepository.existsByEmail("no@email.com");

        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("사용자 이름 존재 여부 확인 - 존재함")
    void shouldReturnTrue_whenUsernameExists() {
        boolean exists = userRepository.existsByUsername("testuser");

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("사용자 이름 존재 여부 확인 - 존재하지 않음")
    void shouldReturnFalse_whenUsernameDoesNotExist() {
        boolean exists = userRepository.existsByUsername("nouser");

        assertThat(exists).isFalse();
    }
}
