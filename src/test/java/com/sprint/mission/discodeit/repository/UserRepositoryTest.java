package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("UserRepository 슬라이스 테스트")
public class UserRepositoryTest {

    @Autowired private UserRepository userRepository;
    @Autowired private UserStatusRepository userStatusRepository;
    @Autowired private TestEntityManager em;

    @BeforeEach
    void setUp() {
        User user1 = userRepository.save(new User("tom", "tom@test.com", "pw123456", null));
        User user2 = userRepository.save(new User("jane", "jane@test.com", "pw234567", null));
        userStatusRepository.save(new UserStatus(user1, Instant.now()));
        userStatusRepository.save(new UserStatus(user2, Instant.now()));

        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("사용자 명으로 사용자 조회 성공")
    void findByUsername() {
        // when
        Optional<User> opt = userRepository.findByUsername("tom");

        // then
        assertThat(opt).isPresent().map(User::getEmail).contains("tom@test.com");
    }

    @Test
    @DisplayName("존재하지 않는 사용자명으로 사용자 조회 실패")
    void findByUsernameWithNotFound() {
        // when
        Optional<User> opt = userRepository.findByUsername("null");

        // then
        assertThat(opt).isEmpty();
    }

    @Test
    @DisplayName("모든 사용자 조회 성공")
    void findAll() {
        // when
        List<User> list = userRepository.findAll();

        // then
        assertThat(list)
            .hasSize(2)
            .extracting(User::getUsername)
            .containsExactlyInAnyOrder("tom", "jane");

        assertThat(list).allMatch(u -> u.getStatus() != null);
    }

    @Test
    @DisplayName("이메일 등록 여부 조회 성공")
    void existsByEmail() {
        // when, then:
        assertThat(userRepository.existsByEmail("tom@test.com")).isTrue();
        assertThat(userRepository.existsByEmail("jane@test.com")).isTrue();
        assertThat(userRepository.existsByEmail("null@test.com")).isFalse();
    }
}