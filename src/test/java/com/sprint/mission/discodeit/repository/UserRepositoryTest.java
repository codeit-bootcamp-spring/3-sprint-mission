
package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("사용자 저장 및 조회 성공")
    void saveAndFind_success() {
        User user = new User("테스트", "test@example.com", "password123");
        userRepository.save(user);

        Optional<User> result = userRepository.findById(user.getId());
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("존재하지 않는 사용자 조회 실패")
    void findById_fail() {
        Optional<User> result = userRepository.findById(UUID.randomUUID());
        assertThat(result).isEmpty();
    }
}
