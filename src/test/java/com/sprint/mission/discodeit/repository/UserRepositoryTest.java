package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.sprint.mission.discodeit.config.TestJpaConfig;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@Import(TestJpaConfig.class)
public class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private EntityManager em;

    @Test
    @DisplayName("사용자를 저장한 뒤, 사용자명으로 조회할 수 있다")
    void findByUsername_should_return_user() {
        // Given
        User user = new User("leejooyong", "jy@example.com", "pw123!!", null);
        userRepository.save(user);

        // When
        Optional<User> result = userRepository.findByUsername("leejooyong");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("leejooyong");
        assertThat(result.get().getEmail()).isEqualTo("jy@example.com");
        assertThat(result.get().getPassword()).isEqualTo("pw123!!");
        assertThat(result.get().getProfile()).isNull();

    }

    @Nested
    @DisplayName("이메일로 사용자 존재 확인")
    class FindByEmailTests {

        @Test
        @DisplayName("사용자를 저장한 뒤, 이메일 존재 여부를 확인할 수 있다")
        void save_then_check_email_exists_should_return_true() {
            // Given
            User user = new User("jake", "jake@example.com", "pw123!!", null);
            userRepository.save(user);

            // When
            boolean exists = userRepository.existsByEmail("jake@example.com");

            // Then
            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("존재하지 않는 이메일로 조화하면 실패한다")
        void save_then_check_email_exists_should_return_false() {
            // Given
            User user = new User("jake", "jake@example.com", "pw123!!", null);
            userRepository.save(user);
            // When
            boolean exists = userRepository.existsByEmail("duke@example.com");

            // Then
            assertThat(exists).isFalse();
        }

    }

    @Nested
    @DisplayName("사용자명으로 사용자 존재 확인")
    class FindByUsernameTests {

        @Test
        @DisplayName("사용자를 저장한 뒤, 사용자명 존재 여부를 확인할 수 있다.")
        void find_by_existent_username_should_return_true() {
            // Given
            User user = new User("jake", "jake@example.com", "pw123!!", null);
            userRepository.save(user);

            // When
            boolean exists = userRepository.existsByUsername("jake");

            // Then
            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("존재하지 않는 username으로 조회하면 실패한다")
        void find_by_nonexistent_username_should_return_empty() {
            // Given
            User user = new User("jake", "jake@example.com", "pw123!!", null);
            userRepository.save(user);

            // When
            boolean exists = userRepository.existsByUsername("duke");

            // Then
            assertThat(exists).isFalse();
        }
    }

    @Test
    @DisplayName("프로필과 상태를 fetch join하여 모든 유저를 조회한다")
    void findAllWithProfileAndStatus_should_return_users_with_profile_and_status() {
        // Given
        BinaryContent profile = createValidBinaryContent();
        User user = new User("tester", "tester@example.com", "pw1234!!", profile);
        em.persist(profile);
        em.persist(user);
        em.persist(new UserStatus(user, Instant.now()));
        em.flush();
        em.clear();

        // When
        List<User> users = userRepository.findAllWithProfileAndStatus();

        // Then
        assertThat(users.size()).isEqualTo(1);
        assertThat(users.get(0).getEmail()).isEqualTo("tester@example.com");
        assertThat(users.get(0).getStatus()).isNotNull();
        assertThat(users.get(0).getProfile().getFileName()).isEqualTo(profile.getFileName());
    }

    public static BinaryContent createValidBinaryContent() {
        return new BinaryContent(
            "sample-image.png",
            2048L,
            "image/png"
        );
    }

}
