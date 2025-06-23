package com.sprint.mission.discodeit.slice.repository;

import com.sprint.mission.discodeit.config.QuerydslConfig;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.jpa.JpaBinaryContentRepository;
import com.sprint.mission.discodeit.repository.jpa.JpaUserRepository;
import com.sprint.mission.discodeit.repository.jpa.JpaUserStatusRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * PackageName  : com.sprint.mission.discodeit.slice
 * FileName     : UserRepositoryTest
 * Author       : dounguk
 * Date         : 2025. 6. 20.
 */
@DataJpaTest
@ActiveProfiles("test")
@Import(QuerydslConfig.class)
@DisplayName("User Repository 테스트")
public class UserRepositoryTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private JpaUserRepository userRepository;

    @Autowired
    private JpaBinaryContentRepository binaryContentRepository;

    @Autowired
    private JpaUserStatusRepository userStatusRepository;

    private BinaryContent binaryContent;
    private User user;
    private UserStatus userStatus;

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

        userStatus = UserStatus.builder()
            .user(user)
            .lastActiveAt(Instant.now())
            .build();
        userStatusRepository.save(userStatus);
    }

    @Test
    @DisplayName("유저 이름을 통해 유저를 찾을 수 있어야 한다.")
    void whenUserExist_thenShouldFindUser(){
        // given

        // when
        Optional<User> foundUser = userRepository.findByUsername("paul");

        // then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo(user.getUsername());
    }

    @Test
    @DisplayName("유저의 이름을 통해 유저를 찾을 없으면 null을 반환한다.")
    void whenUserNotExist_thenShouldNotFindUser(){
        // given

        // when
        Optional<User> foundUser = userRepository.findByUsername("daniel");

        // then
        assertThat(foundUser).isNotPresent();
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

    @Test
    @DisplayName("user을 binaryContent, UserStatus와 한번에 가져온다.")
    void whenFindUser_thenShouldFindUserStatusAndBinaryContent(){
        // given
        entityManager.flush();
        entityManager.clear();


        // when
        List<User> result = userRepository.findAllWithBinaryContentAndUserStatus();

        // then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getProfile().getId()).isEqualTo(binaryContent.getId());

        assertThat(result.get(0).getStatus().getId()).isEqualTo(userStatus.getId());
    }

    @Test
    @DisplayName("유저가 없을경우 빈 리스트를 반환한다.")
    void whenUserNotExist_thenReturnEmptyList(){
        // given
        userRepository.deleteAll();
        long numberOfUsers = userRepository.count();
        // when
        List<User> result = userRepository.findAllWithBinaryContentAndUserStatus();

        // then
        assertThat(numberOfUsers).isEqualTo(result.size());
        assertThat(result).isEmpty();
    }

}
