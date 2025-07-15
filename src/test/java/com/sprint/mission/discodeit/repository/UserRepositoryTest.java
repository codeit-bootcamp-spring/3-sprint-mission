package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.BinaryContent;
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
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@EnableJpaAuditing
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User savedUser;

    @BeforeEach
    void setUp() {
        BinaryContent profile = new BinaryContent("이미지", 1024L, "image/png");
        User user = new User("testuser", "test@abc.com", "1234", profile);
        UserStatus status = new UserStatus(user, Instant.now());
        savedUser = userRepository.save(user);
    }

    @Test
    @DisplayName("username 사용자 조회 성공")
    void findByUsername_success() {
        //given
        String username = "testuser";

        //when
        Optional<User> result = userRepository.findByUsername(username);

        //then
        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo(username);
    }

    @Test
    @DisplayName("username 사용자 조회 실패")
    void findByUsername_notFound() {
        //given
        String username = "empty";

        //when
        Optional<User> result = userRepository.findByUsername(username);

        //then
        assertThat(result).isNotPresent();
    }

    @Test
    @DisplayName("email 사용자 존재 여부 확인 성공")
    void existsByEmail_true() {
        //given
        String email = "test@abc.com";

        //when
        boolean exists = userRepository.existsByEmail(email);

        //then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("email 사용자 존재 여부 확인 실패")
    void existsByEmail_false() {
        //given
        String email = "not@email.com";

        //when
        boolean exists = userRepository.existsByEmail(email);

        //then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("username 존재 여부 확인 실패")
    void existsByUsername_false() {
        //given
        String username = "empty";

        //when
        boolean exists = userRepository.existsByUsername(username);

        //then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("전체 사용자 조회 성공")
    void findAllUsers_containstestuser() {
        //when
        List<User> all = userRepository.findAll();

        //then
        assertThat(all).contains(savedUser);
        assertThat(all.get(0).getStatus()).isNotNull();
        assertThat(all.get(0).getProfile()).isNotNull();
    }
}