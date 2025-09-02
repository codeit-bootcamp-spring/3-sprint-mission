package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.auth.service.DiscodeitUserDetails;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.user.UserRequestDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.enums.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        Optional<User> foundUser = userRepository.findById(result.id());
        assertTrue(foundUser.isPresent(), "유저가 저장되어야 한다.");
        assertEquals("test", foundUser.get().getUsername());
        BinaryContent userProfile = foundUser.get().getProfile();
        assertNotNull(userProfile, "프로필 사진이 등록되어야 한다.");
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
            .role(Role.USER)
            .profile(profile)
            .build();

        User savedUser = userRepository.save(user);
        BinaryContent savedProfile = binaryContentRepository.save(profile);

        UUID userId = savedUser.getId();
        UUID profileId = savedProfile.getId();

        UserResponseDto userResponseDto = new UserResponseDto(userId, "test", "test.com", null,
            true, Role.USER);
        DiscodeitUserDetails userDetails = new DiscodeitUserDetails(userResponseDto, "pwd1234");
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
            userDetails, null, List.of()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        userService.deleteById(userId);

        // then
        Optional<User> foundUser = userRepository.findById(userId);
        assertTrue(foundUser.isEmpty());
        Optional<BinaryContent> foundProfile = binaryContentRepository.findById(profileId);
        assertTrue(foundProfile.isEmpty(), "삭제된 유저의 프로필 사진도 삭제되어야 한다.");
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
