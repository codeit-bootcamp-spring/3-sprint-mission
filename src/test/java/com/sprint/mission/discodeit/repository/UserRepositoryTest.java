package com.sprint.mission.discodeit.repository;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@EnableJpaAuditing
@ActiveProfiles("test")
@DisplayName("유저 Repo 슬라이스 테스트")
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserStatusRepository userStatusRepository;

    @Autowired
    private BinaryContentRepository binaryContentRepository;

    @Test
    @DisplayName("유저 저장 - case : success")
    void saveUserSuccess() {
        // Given
        User user = new User("김현기","test@test.com","009874", null);

        // When
        User savedUser = userRepository.save(user);

        // Then
        assertNotNull(savedUser.getId());
        assertEquals("김현기", savedUser.getUsername());
        assertEquals("test@test.com", savedUser.getEmail());

    }

    @Test
    @DisplayName("유저 저장 - case : 이메일 누락으로 인한 failed")
    void saveUserFail() {
        // Given
        User user = new User("김현기", null, "009874", null);

        // When
        Executable executable = () -> userRepository.saveAndFlush(user);

        // Then
        assertThrows(DataIntegrityViolationException.class, executable);
    }

    @Test
    @DisplayName("유저 모두 조회 - case : success")
    void findAllWithProfileAndStatusSuccess() {
        // Given
        BinaryContent profile = binaryContentRepository.save(
            new BinaryContent("img001.jpg", (long) "이미지파일".getBytes().length, "image/jpg"));
            User user = new User("김현기","test@test.com","009874", profile);
            userRepository.save(user);
            UserStatus userStatus = userStatusRepository.save(
                new UserStatus(user, Instant.now()));
            userStatusRepository.save(userStatus);

        // When
        List<User> users = userRepository.findAllWithProfileAndStatus();

        // Then
        assertEquals(1, users.size());
        User result = users.get(0);
        assertNotNull(result.getProfile());
        assertNotNull(result.getStatus());
        assertEquals("img001.jpg",result.getProfile().getFileName());
        assertEquals(userStatus, result.getStatus());

    }

    @Test
    @DisplayName("유저 모두 조회 - case : 상태는 있으나 프로필이 없는 경우로 인한 failed")
    void findAllWithProfileAndStatusFail() {
        // Given
        User user = new User("김현기", "test@test.com", "009874", null);
        userRepository.save(user);
        UserStatus status = new UserStatus(user, Instant.now());
        userStatusRepository.save(status);

        // When
        List<User> users = userRepository.findAllWithProfileAndStatus();

        // Then
        assertNull(users.get(0).getProfile());
    }
}
