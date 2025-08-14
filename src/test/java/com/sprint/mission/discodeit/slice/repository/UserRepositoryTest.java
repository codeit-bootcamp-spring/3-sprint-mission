package com.sprint.mission.discodeit.slice.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.jpa.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.jpa.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * PackageName  : com.sprint.mission.discodeit.slice
 * FileName     : UserRepositoryTest
 * Author       : dounguk
 * Date         : 2025. 6. 20.
 */
@Testcontainers
//@DataJpaTest
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
//@Import(QuerydslConfig.class)
@DisplayName("User Repository 테스트")
public class UserRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres =
        new PostgreSQLContainer<>("postgres:17");

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry reg) {
        reg.add("spring.datasource.url",      postgres::getJdbcUrl);
        reg.add("spring.datasource.username", postgres::getUsername);
        reg.add("spring.datasource.password", postgres::getPassword);
        reg.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
    }

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BinaryContentRepository binaryContentRepository;

    private BinaryContent binaryContent;
    private User user;

    @BeforeEach
    void setUp(TestInfo testInfo) {
        if (testInfo.getDisplayName().equals("유저가 없을경우 빈 리스트를 반환한다.")) {
            return;
        }

        binaryContent = BinaryContent.builder()
            .size(5L)
            .extension(".png")
            .fileName("test.png")
            .contentType("image/png")
            .build();
        binaryContentRepository.save(binaryContent);

        user = User.builder()
            .username("paul")
            .password("1234")
            .profile(binaryContent)
            .email("paul@gmail.com")
            .build();
        userRepository.save(user);
    }

    @Test
    @DisplayName("중복되는 유저가 있을 경유 true를 반환한다.")
    void whenUsernameNotUnique_thenShouldReturnTrue(){
        // given

        // when
        boolean result = userRepository.existsByUsername("paul");

        // then
        assertThat(result).isTrue();
    }
    @Test
    @DisplayName("중복되는 유저가 없을 경우 false를 반환한다.")
    void whenUsernameNotUnique_thenShouldReturnFalse(){
        // given

        // when
        boolean result = userRepository.existsByUsername("daniel");

        // then
        assertThat(result).isFalse();
    }
    @Test
    @DisplayName("중복되는 email이 있을 경유 true를 반환한다.")
    void whenEmailNotUnique_thenShouldReturnTrue(){
        // given

        // when
        boolean result = userRepository.existsByEmail("paul@gmail.com");

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("중복되는 유저가 없을 경우 false를 반환한다.")
    void whenEmailNotUnique_thenShouldReturnFalse(){
        // given

        // when
        boolean result = userRepository.existsByEmail("daniel@gmail.com");

        // then
        assertThat(result).isFalse();
    }
}
