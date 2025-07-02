package com.sprint.mission.discodeit.integration;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.service.UserService;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UserApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    private MockMultipartFile profileImage;
    private UserCreateRequest userCreateRequest;
    private UserUpdateRequest userUpdateRequest;

    @BeforeEach
    void setUp() {
        profileImage = new MockMultipartFile("profile", "image.jpg", MediaType.IMAGE_JPEG_VALUE,
            "이미지".getBytes());

        userCreateRequest = new UserCreateRequest("testuser", "test@abc.com", "1234");
        userUpdateRequest = new UserUpdateRequest("updateduser", "update@abc.com", "12345");
    }

    @Test
    @DisplayName("사용자 생성 성공")
    void createUser_Success() throws Exception {
        MockMultipartFile userCreateRequestPart = new MockMultipartFile(
            "userCreateRequest", "", MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsBytes(userCreateRequest)
        );

        mockMvc.perform(multipart("/api/users")
                .file(userCreateRequestPart)
                .file(profileImage)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.email").value("test@abc.com"))
            .andExpect(jsonPath("$.username").value("testuser"))
            .andExpect(jsonPath("$.profile.fileName").value("image.jpg"));
    }

    @Test
    @DisplayName("사용자 생성 실패")
    void createUser_Failure() throws Exception {
        UserCreateRequest invalidRequest = new UserCreateRequest("a", "b", "c");

        MockMultipartFile userCreateRequestPart = new MockMultipartFile(
            "userCreateRequest", "", MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsBytes(invalidRequest)
        );

        mockMvc.perform(multipart("/api/users")
                .file(userCreateRequestPart)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("전체 사용자 조회 성공")
    void findAllUsers_Success() throws Exception {
        UserCreateRequest userCreateRequest2 = new UserCreateRequest("user2", "user2@abc.com",
            "1234");

        userService.createUser(userCreateRequest, Optional.empty());
        userService.createUser(userCreateRequest2, Optional.empty());

        mockMvc.perform(get("/api/users")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].username").value("testuser"))
            .andExpect(jsonPath("$[0].email").value("test@abc.com"))
            .andExpect(jsonPath("$[1].username").value("user2"))
            .andExpect(jsonPath("$[1].email").value("user2@abc.com"));
    }

    @Test
    @DisplayName("사용자 수정 성공")
    void updateUser_Success() throws Exception {
        UserDto createdUser = userService.createUser(userCreateRequest, Optional.empty());
        UUID userId = createdUser.id();

        MockMultipartFile userUpdateRequestPart = new MockMultipartFile(
            "userUpdateRequest",
            "",
            MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsBytes(userUpdateRequest)
        );

        mockMvc.perform(multipart("/api/users/{userId}", userId)
                .file(userUpdateRequestPart)
                .file(profileImage)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .with(req -> {
                    req.setMethod("PATCH");
                    return req;
                }))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(userId.toString()))
            .andExpect(jsonPath("$.username").value("updateduser"))
            .andExpect(jsonPath("$.email").value("update@abc.com"))
            .andExpect(jsonPath("$.profile.fileName").value("image.jpg"));
    }

    @Test
    @DisplayName("사용자 수정 실패")
    void updateUser_Failure() throws Exception {
        UUID nonExistentUserId = UUID.randomUUID();

        MockMultipartFile userUpdateRequestPart = new MockMultipartFile(
            "userUpdateRequest",
            "",
            MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsBytes(userUpdateRequest)
        );

        mockMvc.perform(multipart("/api/users/{userId}", nonExistentUserId)
                .file(userUpdateRequestPart)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .with(request -> {
                    request.setMethod("PATCH");
                    return request;
                }))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("사용자 삭제 성공")
    void deleteUser_Success() throws Exception {
        UserDto createdUser = userService.createUser(userCreateRequest, Optional.empty());
        UUID userId = createdUser.id();

        mockMvc.perform(delete("/api/users/{userId}", userId))
            .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("사용자 삭제 실패")
    void deleteUser_Failure() throws Exception {
        UUID nonExistentUserId = UUID.randomUUID();

        mockMvc.perform(delete("/api/users/{userId}", nonExistentUserId))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("사용자 상태 수정 성공")
    void updateUserStatus_Success() throws Exception {
        UserDto createdUser = userService.createUser(userCreateRequest, Optional.empty());
        UUID userId = createdUser.id();

        Instant newLastActiveAt = Instant.now();
        UserStatusUpdateRequest request = new UserStatusUpdateRequest(
            newLastActiveAt
        );

        mockMvc.perform(patch("/api/users/{userId}/userStatus", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.lastActiveAt").value(newLastActiveAt.toString()));
    }

    @Test
    @DisplayName("사용자 상태 수정 실패")
    void updateUserStatus_Failure() throws Exception {
        UUID nonExistentUserId = UUID.randomUUID();
        UserStatusUpdateRequest request = new UserStatusUpdateRequest(
            Instant.now()
        );

        mockMvc.perform(patch("/api/users/{userId}/userStatus", nonExistentUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

}