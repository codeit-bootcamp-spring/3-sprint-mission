package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;


@ActiveProfiles("test")
@DataJpaTest
@DisplayName("UserRepository 슬라이스 테스트")
public class UserRepositoryTest {

    @Autowired private UserRepository userRepository;

    @Autowired
    private TestEntityManager em;

    private User user;

    @BeforeEach
    void setUp() {
        // given: 연관된 엔티티 먼저 저장
        BinaryContent profile = new BinaryContent("file2", 100L, "png");
        em.persist(profile);

        user = new User("test", "email@test.com", "00000000", profile);
        em.persist(user);

        UserStatus status = new UserStatus(user, Instant.now());
        em.persist(status);

        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("유저를 저장하고 조회할 수 있다")
    void saveAndFindUser() {
        // given
        User user = new User("testUser", "test@gmail.com", "00000000", null);

        // when
        User result = userRepository.save(user);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("testUser");
        assertThat(result.getEmail()).isEqualTo("test@gmail.com");
    }

    @Test
    @DisplayName("모든 유저를 조회할 수 있다")
    void findAllUsers() {
        // when
        List<User> result = userRepository.findAll();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserStatus()).isNotNull();
        assertThat(result.get(0).getProfile()).isNotNull();
    }

    @Test
    @DisplayName("유저 ID를 통해 유저를 조회할 수 있다")
    void findUserById() {
        // when
        user = userRepository.findById(user.getId()).orElse(null);

        // then
        assertThat(user).isNotNull();
        assertThat(user.getUsername()).isEqualTo("test");
    }

    @Test
    @DisplayName("존재하지 않는 유저 ID 조회 시 실패")
    void findUserByNotExistingId() {
        // given
        UUID uuid = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");

        // when
        Optional<User> result = userRepository.findById(uuid);

        // then
        assertThat(result).isEmpty();
    }
}
