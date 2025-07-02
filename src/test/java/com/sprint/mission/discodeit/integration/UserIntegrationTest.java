package com.sprint.mission.discodeit.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.User.UserCreateRequest;
import com.sprint.mission.discodeit.dto.User.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.UserStatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
@DisplayName("User API 통합 테스트")
public class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserStatusRepository userStatusRepository;
    @Autowired
    private BinaryContentRepository binaryContentRepository;
    
    private User savedUser;
    private UserStatus savedUserStatus;
    private BinaryContent savedProfile;

    @BeforeEach
    void setUp() {
        savedProfile = new BinaryContent("profile.png", 1024L, "image/png");
        binaryContentRepository.save(savedProfile);

        savedUser = new User("테스트유저", "test@codeit.com", "test1234", savedProfile, null);
        userRepository.save(savedUser);

        savedUserStatus = new UserStatus(savedUser,
                Instant.now().minus(Duration.ofMinutes(6)));  //offline 상태
        userStatusRepository.save(savedUserStatus);
    }

    @Test
    @DisplayName("유저 생성 요청 시 201 응답과 함께 생성된 유저 정보를 반환한다.")
    void createUser_Success() throws Exception {

        // given
        String userName = "생성테스트유저";
        String email = "createTest@codeit.com";
        String password = "createTest1234";

        UserCreateRequest createRequest = new UserCreateRequest(userName, email, password);

        MockMultipartFile jsonPart = new MockMultipartFile(
                "userCreateRequest",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(createRequest)
        );

        // when & then
        mockMvc.perform(multipart("/api/users")
                        .file(jsonPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value(userName))
                .andExpect(jsonPath("$.email").value(email));
    }

    @Test
    @DisplayName("유저 수정 요청 시 200 응답과 함께 수정된 채널 정보를 반환한다.")
    void updateUser_Success() throws Exception {

        // given
        UUID userId = savedUser.getId();
        String newUserName = "(수정)테스트유저";
        String newEmail = "updateTest@codeit.com";
        String newPassword = "updateTest1234";

        UserUpdateRequest updateRequest = new UserUpdateRequest(newUserName, newEmail, newPassword);

        MockMultipartFile jsonPart = new MockMultipartFile(
                "userUpdateRequest",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(updateRequest)
        );

        MockMultipartFile profilePart = new MockMultipartFile(
                "profile",
                "profile2.jpg",
                "image/jpeg",
                "테스트이미지바이트".getBytes()
        );

        // when & then
        mockMvc.perform(multipart(HttpMethod.PATCH, "/api/users/" + userId)
                        .file(jsonPart)
                        .file(profilePart)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.username").value(newUserName))
                .andExpect(jsonPath("$.email").value(newEmail));

        User updatedUser = userRepository.findById(userId).orElse(null);
        assertThat(updatedUser.getPassword()).isEqualTo(newPassword);
        assertThat(updatedUser.getProfile().getFileName()).isEqualTo("profile2.jpg");
        assertThat(updatedUser.getProfile().getContentType()).isEqualTo("image/jpeg");
        assertThat(updatedUser.getProfile().getSize()).isEqualTo("테스트이미지바이트".getBytes().length);
    }

    @Test
    @DisplayName("유저 삭제 요청 시 204 응답과 함께 채널이 삭제된다.")
    void 유저삭제_성공() throws Exception {

        // given
        UUID userId = savedUser.getId();

        // when & then
        mockMvc.perform(delete("/api/users/" + userId.toString()))
                .andExpect(status().isNoContent());
        assertThat(userRepository.findById(userId).isPresent()).isFalse();
    }

    @Test
    @DisplayName("유저 전체조회 요청시 200 응답과 함께 유저 목록이 반환된다.")
    void 유저전체조회_성공() throws Exception {

        // given - @BeforeEach를 통해 DB에 저장된 사용자

        // when & then
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(savedUser.getId().toString()))
                .andExpect(jsonPath("$[0].username").value(savedUser.getUsername()))
                .andExpect(jsonPath("$[0].email").value(savedUser.getEmail()))
                .andExpect(jsonPath("$[0].profile.id").value(
                        savedUser.getProfile().getId().toString()))
                .andExpect(jsonPath("$[0].profile.fileName").value(
                        savedUser.getProfile().getFileName()))
                .andExpect(jsonPath("$[0].profile.contentType").value(
                        savedUser.getProfile().getContentType()));
    }

    @Test
    @DisplayName("유저 온라인 상태 업데이트 요청시 200 응답과 업데이트된 유저 상태가 반환된다.")
    void 유저온라인상태업데이트_성공() throws Exception {

        // given
        UUID userId = savedUser.getId();
        UUID userStatusId = savedUserStatus.getId();
        Instant now = Instant.now();
        UserStatusUpdateRequest updateRequest = new UserStatusUpdateRequest(now);

        // when & then
        mockMvc.perform(patch("/api/users/" + userId.toString() + "/userStatus")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsBytes(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userStatusId.toString()))
                .andExpect(jsonPath("$.lastActiveAt").value(now.toString()));
        assertThat(savedUserStatus.isOnline()).isTrue();
    }
}
