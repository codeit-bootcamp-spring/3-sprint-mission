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
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@EnableJpaAuditing
@DisplayName("UserRepository 테스트")
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User testUser1;
    private User testUser2;

    @BeforeEach
    void setUp() {
        // User 생성
        testUser1 = new User("testuser1", "test1@example.com", "password1", null);
        testUser2 = new User("testuser2", "test2@example.com", "password2", null);

        // UserStatus 생성 및 연관관계 설정
        UserStatus status1 = new UserStatus(testUser1, Instant.now().minusSeconds(60));
        UserStatus status2 = new UserStatus(testUser2, Instant.now().minusSeconds(600));

        entityManager.persistAndFlush(testUser1);
        entityManager.persistAndFlush(testUser2);
    }

    @Test
    @DisplayName("사용자명으로 사용자 조회 - 성공")
    void findByUsername_Success() {
        // when
        Optional<User> result = userRepository.findByUsername("testuser1");

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("testuser1");
        assertThat(result.get().getEmail()).isEqualTo("test1@example.com");
    }

    @Test
    @DisplayName("사용자명으로 사용자 조회 - 실패 (존재하지 않는 사용자)")
    void findByUsername_NotFound() {
        // when
        Optional<User> result = userRepository.findByUsername("nonexistent");

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("이메일 존재 여부 확인 - 성공 (존재함)")
    void existsByEmail_True() {
        // when
        boolean exists = userRepository.existsByEmail("test1@example.com");

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("이메일 존재 여부 확인 - 실패 (존재하지 않음)")
    void existsByEmail_False() {
        // when
        boolean exists = userRepository.existsByEmail("nonexistent@example.com");

        // then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("사용자명 존재 여부 확인 - 성공 (존재함)")
    void existsByUsername_True() {
        // when
        boolean exists = userRepository.existsByUsername("testuser1");

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("사용자명 존재 여부 확인 - 실패 (존재하지 않음)")
    void existsByUsername_False() {
        // when
        boolean exists = userRepository.existsByUsername("nonexistent");

        // then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("프로필과 상태를 포함한 모든 사용자 조회 - 성공")
    void findAllWithProfileAndStatus_Success() {
        // when
        List<User> users = userRepository.findAllWithProfileAndStatus();

        // then
        assertThat(users).hasSize(2);
        assertThat(users).extracting(User::getUsername)
            .contains("testuser1", "testuser2");
    }

    @Test
    @DisplayName("프로필과 상태를 포함한 모든 사용자 조회 - 빈 결과")
    void findAllWithProfileAndStatus_Empty() {
        // given
        entityManager.getEntityManager()
            .createQuery("DELETE FROM UserStatus")
            .executeUpdate();
        entityManager.getEntityManager()
            .createQuery("DELETE FROM User")
            .executeUpdate();
        entityManager.flush();

        // when
        List<User> users = userRepository.findAllWithProfileAndStatus();

        // then
        assertThat(users).isEmpty();
    }
}