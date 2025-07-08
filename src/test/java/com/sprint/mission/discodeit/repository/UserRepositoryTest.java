package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.config.JpaConfig;
import com.sprint.mission.discodeit.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(JpaConfig.class)
@ActiveProfiles("test")
@DisplayName("UserRepository 기능 테스트")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    // 테스트에 사용할 더미 데이터
    @BeforeEach
    void setUp() {

        // given
        User user1 = User.builder()
                .username("test1")
                .email("test1@test.com")
                .password("pwd1234")
                .build();

        User user2 = User.builder()
                .username("test2")
                .email("test2@test.com")
                .password("pwd12345")
                .build();

        userRepository.save(user1);
        userRepository.save(user2);
    }

    @Test
    @DisplayName("사용자명으로 사용자를 조회할 수 있어야 한다.")
    void givenUsername_whenFindByUsername_thenReturnUser() {

        // when
        Optional<User> foundUser = userRepository.findByUsername("test1");

        // then
        assertTrue(foundUser.isPresent());
        assertEquals("test1", foundUser.get().getUsername());
    }

    @Test
    @DisplayName("이메일로 사용자를 조회할 수 있어야 한다.")
    void givenEmail_whenFindByEmail_thenReturnUser() {

        // when
        Optional<User> foundUser = userRepository.findByEmail("test2@test.com");

        // then
        assertTrue(foundUser.isPresent());
        assertEquals("test2@test.com", foundUser.get().getEmail());
    }

    @Test
    @DisplayName("존재하는 사용자명 여부를 확인할 수 있어야 한다.")
    void givenUsername_whenExistsByUsername_thenReturnTrue() {

        // when
        boolean exist = userRepository.existsByUsername("test2");

        // then
        assertTrue(exist);
    }

    @Test
    @DisplayName("존재하지 않는 사용자명 여부를 확인할 수 있어야 한다.")
    void givenNonexistentUsername_whenExistsByUsername_thenReturnFalse() {

        // when
        boolean exist = userRepository.existsByUsername("notExistName");

        // then
        assertFalse(exist);
    }

    @Test
    @DisplayName("존재하는 이메일 여부를 확인할 수 있어야 한다.")
    void givenUsername_whenExistsByEmail_thenReturnTrue() {

        // when
        boolean exist = userRepository.existsByEmail("test1@test.com");

        // then
        assertTrue(exist);
    }
}