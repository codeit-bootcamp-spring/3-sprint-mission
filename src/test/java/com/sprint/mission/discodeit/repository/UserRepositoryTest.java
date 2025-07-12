package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.config.JpaAuditingConfig;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@Import(JpaAuditingConfig.class)
@ActiveProfiles("test") // application-test.yaml 사용
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BinaryContentRepository binaryContentRepository;

    @Autowired
    private UserStatusRepository userStatusRepository;

    @Autowired
    EntityManager em;

    @Test
    @DisplayName("사용자 저장 및 findByUsername 성공")
    void findByUsername_success() {
        // given
        User user = new User("testuser", "test@example.com", "password", null);
        userRepository.saveAndFlush(user);

        // when
        Optional<User> result = userRepository.findByUsername("testuser");

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("존재하지 않는 사용자 조회 실패")
    void findByUsername_fail() {
        // when
        Optional<User> result = userRepository.findByUsername("unknown");

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("이메일/아이디 존재 여부 검사")
    void existsByEmailAndUsername() {
        // given
        User user = new User("testuser", "test@example.com", "password", null);
        userRepository.save(user);

        // when
        boolean existsEmail = userRepository.existsByEmail("test@example.com");
        boolean existsUsername = userRepository.existsByUsername("testuser");

        // then
        assertThat(existsEmail).isTrue();
        assertThat(existsUsername).isTrue();
    }

    @Test
    @DisplayName("사용자 전체 조회 - 프로필, 상태 포함")
    void findAllWithProfileAndStatus() {
        // given
        BinaryContent profile = new BinaryContent("test.jpg", 100L, "image/jpeg");
        binaryContentRepository.save(profile);

        User user = new User("tester", "email@domain.com", "pw", profile);
        userRepository.save(user);

        UserStatus status = new UserStatus(user, Instant.now());
        userStatusRepository.save(status);

        // when
        List<User> users = userRepository.findAllWithProfileAndStatus();

        // then
        assertThat(users).hasSize(1);
        assertThat(users.get(0).getProfile()).isNotNull();
        assertThat(users.get(0).getStatus()).isNotNull();
    }

    // 제약 조건 검증

    @Test
    @DisplayName("프로필 이미지 삭제 시 User의 profile_id가 null로 설정되는지 확인")
    void profileDeletionSetsUserProfileIdToNull() {
        // given
        BinaryContent profile = new BinaryContent("to-delete.jpg", 200L, "image/jpeg");
        binaryContentRepository.save(profile);

        User user = new User("deluser", "del@exam.com", "pw", profile);
        userRepository.save(user);

        // 연관 끊기 👇
        user.clearProfile();
        userRepository.save(user);

        // when
        binaryContentRepository.delete(profile);

        // then
        em.flush();
        em.clear();

        User fetchedUser = userRepository.findByUsername("deluser").orElseThrow();
        assertThat(fetchedUser.getProfile()).isNull();
    }

    @Test
    @DisplayName("User 삭제 시 UserStatus도 함께 삭제되는지 확인")
    void deleteUserAlsoDeletesUserStatus() {
        // given
        User user = new User("deleteMe", "gone@example.com", "pass", null);
        userRepository.save(user);

        UserStatus status = new UserStatus(user, Instant.now());
        userStatusRepository.save(status);

        // when
        userRepository.delete(user);

        // then
        boolean exists = userStatusRepository.findAll().stream()
            .anyMatch(s -> s.getUser().getId().equals(user.getId()));
        assertThat(exists).isFalse();
    }


}