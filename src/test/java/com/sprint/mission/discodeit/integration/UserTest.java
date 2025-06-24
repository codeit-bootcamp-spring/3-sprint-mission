package com.sprint.mission.discodeit.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.sprint.mission.discodeit.dto.user.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.helper.FileUploadUtils;
import com.sprint.mission.discodeit.repository.jpa.JpaBinaryContentRepository;
import com.sprint.mission.discodeit.repository.jpa.JpaUserRepository;
import com.sprint.mission.discodeit.repository.jpa.JpaUserStatusRepository;
import com.sprint.mission.discodeit.unit.basic.BasicUserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileSystemUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * PackageName  : com.sprint.mission.discodeit.integration
 * FileName     : UserTest
 * Author       : dounguk
 * Date         : 2025. 6. 23.
 */
@DisplayName("User 통합 테스트")
@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(
    properties = {
        "file.upload.all.path=${java.io.tmpdir}/upload-test"
    }
)
public class UserTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JpaBinaryContentRepository binaryContentRepository;

    @Autowired
    private JpaUserStatusRepository userStatusRepository;

    @Autowired
    private JpaUserRepository userRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private BasicUserService userService;

    @Autowired
    private FileUploadUtils fileUploadUtils;

    private Path tempDir;

    @BeforeEach
    void setUp() throws IOException {
        tempDir = Files.createTempDirectory("upload-test-");
        ReflectionTestUtils.setField(fileUploadUtils, "path", tempDir.toString());
    }

    @AfterEach
    void tearDown() throws IOException {
        FileSystemUtils.deleteRecursively(tempDir);
    }

    @Test
    @DisplayName("유저 생성 프로세스가 모든 계층에서 올바르게 동작해야 한다.")
    void userCreate_success() throws Exception {
        // given
        UserCreateRequest request = UserCreateRequest.builder()
            .username("paul")
            .email("paul@test.com")
            .password("1234")
            .build();

        MockMultipartFile jsonPart = new MockMultipartFile(
            "userCreateRequest",
            "",
            MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsBytes(request)
        );

        byte[] img = new byte[]{1, 2};
        MockMultipartFile imgPart = new MockMultipartFile(
            "profile", "avatar.png", MediaType.IMAGE_PNG_VALUE, img);

        // when
        String responseBody = mockMvc.perform(
                multipart("/api/users")
                    .file(jsonPart).file(imgPart)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.profile").isNotEmpty())
            .andReturn()
            .getResponse()
            .getContentAsString();

        // then
        // 유저 생성 확인 + binaryContent 확인 + UserStatus 확인
        String userIdStr = JsonPath.read(responseBody, "$.id");
        String profileIdStr = JsonPath.read(responseBody, "$.profile.id");
        String fileName = JsonPath.read(responseBody, "$.profile.fileName");

        UUID userId = UUID.fromString(userIdStr);
        UUID profileId = UUID.fromString(profileIdStr);

        assertThat(binaryContentRepository.findById(profileId)).isPresent();

        User savedUser = userRepository.findById(userId).orElseThrow();
        assertThat(savedUser.getProfile().getId()).isEqualTo(profileId);

        assertThat(fileName).isEqualTo("avatar.png");

        assertThat(savedUser.getStatus()).isNotNull();
        assertThat(savedUser.getStatus().getUser().getId()).isEqualTo(userId);
    }

    @Test
    @DisplayName("유저정보중 username 또는 email이 중복될경우 계정 생성을 실패한다.")
    void createUser_sameInfo_throwException() throws Exception {
        // given
        userRepository.save(new User("paul", "a@a.com", "1234"));

        UserCreateRequest request = new UserCreateRequest("paul", "b@b.com", "4321");

        MockMultipartFile jsonPart = new MockMultipartFile(
            "userCreateRequest",
            "",
            MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsBytes(request)
        );

        mockMvc.perform(
                multipart("/api/users")
                    .file(jsonPart)
                    .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("USER_ALREADY_EXISTS"))
            .andExpect(jsonPath("$.details.username").value("paul"));
    }

    @Test
    @DisplayName("유저 삭제 프로세스가 모든 계층에 올바르게 동작해야 한다.")
    void deleteUser_hasProfile_success() throws Exception {
        // given
        BinaryContent profile = binaryContentRepository.save(
            new BinaryContent("avatar.png", 2L, "image/png", ".png")
        );

        Path profileDir  = tempDir.resolve("img");
        Files.createDirectories(profileDir);
        Path profileFile = profileDir.resolve(profile.getId() + ".png");
        Files.write(profileFile, new byte[] { 1, 2 });

        User user = User.builder()
            .username("paul")
            .email("paul@test.com")
            .password("1234")
            .profile(profile)
            .build();
        userRepository.save(user);
        UUID userId = user.getId();

        userStatusRepository.save(new UserStatus(user));

        // when
        userService.deleteUser(userId);

        // then
        // binaryContent + userStatus 삭제
        assertThat(userRepository.findById(userId)).isEmpty();
        assertThat(binaryContentRepository.findById(profile.getId())).isEmpty();
        assertThat(userStatusRepository.findById(userId)).isEmpty();

        assertThat(Files.exists(profileFile)).isFalse();
    }

    @Test
    @DisplayName("프로핑 사진이 없을 때 유저 삭제 프로세스가 모든 계층에 올바르게 동작해야 한다.")
    void deleteUser_noProfile_success() throws Exception {
        // given
        User user = User.builder()
            .username("paul")
            .email("paul@test.com")
            .password("1234")
            .build();
        userRepository.save(user);
        UUID userId = user.getId();

        userStatusRepository.save(new UserStatus(user));

        // when
        userService.deleteUser(userId);

        // then
        assertThat(userRepository.findById(userId)).isEmpty();
        assertThat(userStatusRepository.findById(userId)).isEmpty();
    }

    @Test
    @DisplayName("기존 프로필을 새 이미지로 교체해야 한다.")
    void updateUser_replaceProfile_success() throws Exception {

        BinaryContent oldProfile = binaryContentRepository.save(
            new BinaryContent("old.png", 1L, "image/png", ".png"));
        Path dir = tempDir.resolve("img");
        Files.createDirectories(dir);
        Path oldFilePath = dir.resolve(oldProfile.getId() + ".png");
        Files.write(oldFilePath, new byte[]{1, 2});

        User user = new User("paul", "paul@test.com", "1234", oldProfile);
        userRepository.save(user);
        UUID userId = user.getId();

        MockMultipartFile newImg = new MockMultipartFile(
            "profile", "new.png", "image/png", new byte[]{9, 9});

        UserUpdateRequest request = new UserUpdateRequest(
            "paul",
            "paul@test.com",
            null
        );

        // when
        userService.update(userId, request, newImg);

        // then
        // BinaryContent 삭제
        assertThat(binaryContentRepository.findById(oldProfile.getId())).isEmpty();
        assertThat(Files.exists(oldFilePath)).isFalse();

        // 새 BinaryContent 저장 및 연동
        User updated = userRepository.findById(userId).orElseThrow();
        assertThat(updated.getProfile()).isNotNull();
        assertThat(updated.getProfile().getId()).isNotEqualTo(oldProfile.getId());
    }

    @Test
    @DisplayName("파일 없이 username 만 변경이 가능해야 한다.")
    void updateUser_changeName_only_success() {
        BinaryContent profile = binaryContentRepository.save(
            new BinaryContent("avatar.png", 2L, "image/png", ".png"));
        User user = new User("oldName", "paul@test.com", "1234", profile);
        userRepository.save(user);

        UUID userId = user.getId();
        UserUpdateRequest req = new UserUpdateRequest(
            "newName",   // 이름만 변경
            "paul@test.com",
            null
        );

        // when
        userService.update(userId, req, null);

        // then
        User updated = userRepository.findById(userId).orElseThrow();
        assertThat(updated.getUsername()).isEqualTo("newName");
        assertThat(updated.getProfile().getId()).isEqualTo(profile.getId());
    }

    @Test
    @DisplayName("이메일이 중복되면 유저 수정은 실패해야 한다.")
    void updateUser_emailAlreadyExist_fail() throws Exception {
        // given
        User user1 = new User("paul", "p@p.com", "1234");
        userRepository.save(user1);

        User user2 = new User("daniel", "d@p.com", "1234");
        userRepository.save(user2);

        UserUpdateRequest request = new UserUpdateRequest("john", "p@p.com", "1234");

        // when
        mockMvc.perform(multipart("/api/users/{userId}", user2.getId())
                .file(new MockMultipartFile(
                    "userUpdateRequest",
                    "",
                    MediaType.APPLICATION_JSON_VALUE,
                    objectMapper.writeValueAsBytes(request)
                ))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON)
                .with(req -> {
                    req.setMethod("PATCH");
                    return req;
                }))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("USER_ALREADY_EXISTS"))
            .andExpect(jsonPath("$.details.email").value("p@p.com"))
            .andExpect(jsonPath("$.message").value("유저가 이미 있습니다."));
    }
}

