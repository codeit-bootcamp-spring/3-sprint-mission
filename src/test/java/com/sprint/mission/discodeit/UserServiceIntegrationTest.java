package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.user.UserRequestDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "discodeit.storage.type=local",
        "discodeit.storage.local.root-path=./binaryTest"
})
@DisplayName("UserService 통합 테스트")
@Transactional
public class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BinaryContentRepository binaryContentRepository;

    @Autowired
    private UserStatusRepository userStatusRepository;

    @Autowired
    private BinaryContentStorage binaryContentStorage;

    @Test
    @DisplayName("사용자 등록 프로세스가 모든 계층에서 올바르게 동작해야 한다.")
    void completeCreateUserIntegration() throws IOException {

        // given
        UserRequestDto request = new UserRequestDto("test", "test@test.com", "pwd1234");
        byte[] imageBytes = "test".getBytes(StandardCharsets.UTF_8);
        BinaryContentDto profile = new BinaryContentDto("profile.png", 3L, "image/png", imageBytes);

        // when
        UserResponseDto result = userService.create(request, profile);

        // then
        assertNotNull(result);

        UUID userId = result.id();

        Optional<User> foundUser = userRepository.findById(userId);
        assertTrue(foundUser.isPresent());
        assertEquals("test", foundUser.get().getUsername());

        Optional<UserStatus> status = userStatusRepository.findByUserId(userId);
        assertTrue(status.isPresent());

        BinaryContent userProfile = foundUser.get().getProfile();
        assertNotNull(userProfile);
        assertEquals("profile.png", userProfile.getFileName());

        byte[] data = binaryContentStorage.get(userProfile.getId()).readAllBytes();
        assertArrayEquals(imageBytes, data);
    }

    @Test
    @DisplayName("사용자 삭제 프로세스가 모든 계층에서 올바르게 동작해야 한다")
    void completeDeleteUserIntegration() {

        // given
        BinaryContent profile = new BinaryContent("profile.png", 3L, "image/png");
        User user = User.builder()
                .username("test")
                .email("test@test.com")
                .password("pwd1234")
                .profile(profile)
                .build();

        UserStatus userStatus = UserStatus.builder()
                .user(user)
                .lastActiveAt(Instant.now())
                .build();

        user.updateStatus(userStatus);

        User savedUser = userRepository.save(user);
        BinaryContent savedProfile = binaryContentRepository.save(profile);
        userStatusRepository.save(userStatus);

        UUID userId = savedUser.getId();
        UUID profileId = savedProfile.getId();

        // when
        userService.deleteById(userId);

        // then
        Optional<User> foundUser = userRepository.findById(userId);
        assertTrue(foundUser.isEmpty());

        Optional<BinaryContent> foundProfile = binaryContentRepository.findById(profileId);
        assertTrue(foundProfile.isEmpty());

        Optional<UserStatus> foundUserStatus = userStatusRepository.findByUserId(userId);
        assertTrue(foundUserStatus.isEmpty());
    }

    /*
        테스트 종료 후 binaryStorage 테스트 파일 삭제 처리
     */
    @AfterEach
    void cleanUpStorage() throws IOException {
        Path testRoot = Paths.get("./binaryTest");
        if (Files.exists(testRoot)) {
            Files.walk(testRoot)
                    .sorted((a, b) -> b.compareTo(a)) // 파일 먼저, 그 다음 디렉토리 삭제
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            // 무시 또는 로깅
                        }
                    });
        }
    }
}
