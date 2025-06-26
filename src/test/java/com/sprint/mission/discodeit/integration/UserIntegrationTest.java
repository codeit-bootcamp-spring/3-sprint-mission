package com.sprint.mission.discodeit.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private UserStatusRepository userStatusRepository;
    @Autowired
    private UserStatusRepository ReadStatusRepository;


    @BeforeEach
    void setup() {
        userRepository.deleteAll();
        userStatusRepository.deleteAll();
        ReadStatusRepository.deleteAll();
    }

    @Test
    @DisplayName("사용자 생성 API 통합 테스트 - 첨부파일 포함")
    void createUser_withProfileImage_success() throws Exception {
        // given
        UserCreateRequest request = new UserCreateRequest(
            "testUser", "test@example.com", "password"
        );

        MockMultipartFile userPart = new MockMultipartFile(
            "userCreateRequest", "", MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsBytes(request)
        );

        MockMultipartFile profilePart = new MockMultipartFile(
            "profile", "test.jpg", MediaType.IMAGE_JPEG_VALUE,
            "image-bytes".getBytes()
        );

        // when & then
        mockMvc.perform(multipart("/api/users")
                .file(userPart)
                .file(profilePart)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.username").value("testUser"))
            .andExpect(jsonPath("$.email").value("test@example.com"))
            .andExpect(jsonPath("$.profile.fileName").value("test.jpg"))
            .andExpect(jsonPath("$.profile.size").value("image-bytes".getBytes().length));
    }

    @Test
    @DisplayName("사용자 생성 API 통합 테스트")
    void createUser_success() throws Exception {
        // given
        UserCreateRequest request = new UserCreateRequest("testUser", "test@example.com",
            "password");

        MockMultipartFile userPart = new MockMultipartFile(
            "userCreateRequest", "", "application/json",
            objectMapper.writeValueAsBytes(request)
        );

        // when & then
        mockMvc.perform(multipart("/api/users")
                .file(userPart)
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.username").value("testUser"))
            .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    @DisplayName("사용자 목록 조회 API 통합 테스트")
    void findAllUsers_success() throws Exception {
        // given
        User user = new User("username", "test@example.com", "password", null);
        userRepository.saveAndFlush(user);

        UserStatus status = new UserStatus(user, Instant.now());
        userStatusRepository.saveAndFlush(status);

        mockMvc.perform(get("/api/users"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].username").value("username"));
    }

    @Test
    @DisplayName("사용자 수정 API 통합 테스트")
    void updateUser_success() throws Exception {
        // given
        User user = new User("oldName", "old@email.com", "password", null);
        userRepository.save(user);

        UserStatus status = new UserStatus(user, Instant.now());
        userStatusRepository.save(status);

        UserUpdateRequest updateRequest = new UserUpdateRequest(
            "newName", "new@email.com", "newPassword"
        );

        MockMultipartFile updatePart = new MockMultipartFile(
            "userUpdateRequest", "", "application/json",
            objectMapper.writeValueAsBytes(updateRequest)
        );

        // when & then

        mockMvc.perform(multipart("/api/users/" + user.getId())
                .file(updatePart)
                .with(req -> {
                    req.setMethod("PATCH");
                    return req;
                })
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("newName"))
            .andExpect(jsonPath("$.email").value("new@email.com"));
    }

    @Test
    @DisplayName("사용자 삭제 API 통합 테스트")
    void deleteUser_success() throws Exception {
        // given
        User user = new User("username", "email@test.com", "password", null);
        userRepository.save(user);

        UserStatus status = new UserStatus(user, Instant.now());
        userStatusRepository.save(status);

        // when & then
        mockMvc.perform(delete("/api/users/" + user.getId()))
            .andExpect(status().isNoContent());

        assertThat(userRepository.findById(user.getId())).isEmpty();
        assertThat(userStatusRepository.findById(user.getId())).isEmpty();
        assertThat(ReadStatusRepository.findById(user.getId())).isEmpty();


    }

    @Test
    @DisplayName("사용자 상태 수정 API 통합 테스트")
    void updateUserStatus_success() throws Exception {
        // given
        User user = new User("username", "email@test.com", "password", null);
        userRepository.save(user);

        UserStatus status = new UserStatus(user, Instant.now());
        userStatusRepository.save(status);

        UserStatusUpdateRequest request = new UserStatusUpdateRequest(Instant.now());

        mockMvc.perform(patch("/api/users/" + user.getId() + "/userStatus")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").exists());
    }
}
