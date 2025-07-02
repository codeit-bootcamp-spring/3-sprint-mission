package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.config.TestJpaAuditConfig;
import com.sprint.mission.discodeit.entity.User;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

@DataJpaTest
@Import(TestJpaAuditConfig.class)
@ActiveProfiles("test")
@DisplayName("UserRepository 단위 테스트")
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        User user = new User("테스트유저", "test@codeit.com", "test1234", null, null);
        userRepository.save(user);
    }

    @Test
    @DisplayName("존재하는 사용자 이름으로 조회 시 사용자 정보를 반환한다.")
    void shouldReturnUser_whenUsernameExists() {

        // given
        String userName = "테스트유저";

        // when
        Optional<User> result = userRepository.findByUsername(userName);

        // then
        assertThat(result.isPresent()).isTrue();
        assertThat(result.get().getUsername()).isEqualTo(userName);
    }

    @Test
    @DisplayName("존재하지 않는 사용자 이름으로 조회 시 빈 값을 반환한다.")
    void shouldReturnEmpty_whenUsernameDoesNotExist() {
        // given
        String userName = "없는 유저";

        // when
        Optional<User> result = userRepository.findByUsername(userName);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("사용자 이름의 존재 여부를 확인한다.")
    void shouldVerifyExistenceOfUsernameCorrectly() {

        // given
        String existingUsername = "테스트유저";
        String nonExistingUsername = "없는 유저";

        // when & then
        assertThat(userRepository.existsByUsername(existingUsername)).isTrue();
        assertThat(userRepository.existsByUsername(nonExistingUsername)).isFalse();
    }

    @Test
    @DisplayName("이메일로 사용자 존재여부를 확인한다.")
    void shouldVerifyExistenceOfEmailCorrectly() {

        // given
        String existingUseremail = "test@codeit.com";
        String nonExistingUserEmail = "test@naver.com";

        // when & then
        assertThat(userRepository.existsByEmail(existingUseremail)).isTrue();
        assertThat(userRepository.existsByEmail(nonExistingUserEmail)).isFalse();
    }
}
