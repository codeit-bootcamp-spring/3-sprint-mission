package com.sprint.mission.discodeit.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.UUID;
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
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("사용자 API 통합 테스트")
class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("사용자 생성 시 Repository까지 반영되어야 한다")
    void givenValidCreateRequest_whenCreateUser_thenUserPersisted() throws Exception {
        // Given
        UserCreateRequest userCreateRequest = new UserCreateRequest(
            "testuser",
            "test@example.com",
            "!password123"
        );

        MockMultipartFile userCreateRequestPart
            = toJsonPart("userCreateRequest", userCreateRequest);

        MockMultipartFile profilePart = fakeImagePart();

        // When & Then
        mockMvc.perform(multipart("/api/users")
                .file(userCreateRequestPart)
                .file(profilePart)
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.username").value("testuser"))
            .andExpect(jsonPath("$.email").value("test@example.com"));

        User user = userRepository.findByUsername("testuser").orElseThrow();
        assertThat(user.getEmail()).isEqualTo("test@example.com");

    }

    @Test
    @DisplayName("중복된 이메일로 사용자 생성 시 400이 되어야 한다")
    void givenDuplicateEmail_whenCreateUser_thenBadRequestReturned() throws Exception {
        // Given
        BinaryContent binaryContent = new BinaryContent(
            "image.png",
            1_024L,
            "image/png"
        );

        User existingUser = new User(
            "olduser",
            "test@example.com",
            "!password123",
            binaryContent
        );
        userRepository.save(existingUser);

        UserCreateRequest userCreateRequest = new UserCreateRequest(
            "newuser",
            "test@example.com",
            "!password123"
        );

        MockMultipartFile userCreateRequestPart
            = toJsonPart("userCreateRequest", userCreateRequest);

        MockMultipartFile profilePart = fakeImagePart();

        // When & Then
        mockMvc.perform(multipart("/api/users")
                .file(userCreateRequestPart)
                .file(profilePart)
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isBadRequest());

    }

    @Test
    @DisplayName("사용자 수정 시 Repository까지 반영되어야 한다")
    void givenExistingUser_whenUpdateUser_thenChangesPersisted() throws Exception {
        // Given
        UUID userId = registerUserAndGetId("olduser", "old@example.com");

        UserUpdateRequest updateRequest = new UserUpdateRequest(
            "updateUser",
            "updated@example.com",
            "!newPassword456"
        );

        MockMultipartFile updateReqPart = toJsonPart("userUpdateRequest", updateRequest);
        MockMultipartFile newProfilePart = fakeImagePart();

        // When & Then
        mockMvc.perform(multipart(HttpMethod.PATCH, "/api/users/{id}", userId)
                .file(updateReqPart)
                .file(newProfilePart)
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("updateUser"))
            .andExpect(jsonPath("$.email").value("updated@example.com"));

        User user = userRepository.findById(userId).orElseThrow();
        assertThat(user.getUsername()).isEqualTo("updateUser");
        assertThat(user.getEmail()).isEqualTo("updated@example.com");
    }

    @Test
    @DisplayName("존재하지 않는 사용자 수정 시 404가 반환되어야 한다")
    void givenNonExistingUser_whenUpdateUser_thenNotFoundReturned() throws Exception {
        // Given
        UserUpdateRequest updateRequest = new UserUpdateRequest(
            "nobody",
            "nobody@example.com",
            "!newPassword456"
        );

        MockMultipartFile updateReqPart = toJsonPart("userUpdateRequest", updateRequest);

        // When & Then
        mockMvc.perform(multipart(HttpMethod.PATCH, "/api/users/{id}", UUID.randomUUID())
                .file(updateReqPart)
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("사용자를 삭제하면 Repository에서 제거되어야 한다")
    void givenExistingUser_whenDeleteUser_thenUserRemoved() throws Exception {
        // Given
        UUID userId = registerUserAndGetId("deleteuser", "delete@example.com");

        // When & Then
        mockMvc.perform(delete("/api/users/{id}", userId))
            .andExpect(status().isNoContent());

        boolean exists = userRepository.existsById(userId);
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("존재하지 않는 사용자를 삭제하면 404 Not Found가 발생해야 한다")
    void givenNonExistingUser_whenDeleteUser_thenNotFoundReturned() throws Exception {
        // Given
        UUID userId = UUID.randomUUID();

        // When & Then
        mockMvc.perform(delete("/api/users/{id}", userId))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("사용자 목록 조회 시 전체 사용자가 반환되어야 한다")
    void givenUsersExist_whenGetUsers_thenFullListReturned() throws Exception {
        // Given
        registerUserAndGetId("user1", "user1@example.com");
        registerUserAndGetId("user2", "user2@example.com");

        // When & Then
        mockMvc.perform(get("/api/users"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].username").exists())
            .andExpect(jsonPath("$[1].username").exists());
    }

    @Test
    @DisplayName("등록된 사용자가 없을 경우 빈 배열이 반환되어야 한다")
    void givenNoUsersExist_whenGetUsers_thenEmptyListReturned() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/users"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(0));
    }

    private UUID registerUserAndGetId(String username, String email) throws Exception {
        UserCreateRequest request = new UserCreateRequest(username, email, "!password123");
        MockMultipartFile requestPart = toJsonPart("userCreateRequest", request);
        MockMultipartFile profile = fakeImagePart();

        String body = mockMvc.perform(multipart("/api/users")
                .file(requestPart)
                .file(profile))
            .andReturn()
            .getResponse()
            .getContentAsString();

        return UUID.fromString(JsonPath.read(body, "$.id"));
    }

    private MockMultipartFile toJsonPart(String partName, Object value) throws Exception {
        return new MockMultipartFile(
            partName,
            partName + ".json",
            "application/json",
            objectMapper.writeValueAsBytes(value)
        );
    }

    private MockMultipartFile fakeImagePart() {
        return new MockMultipartFile(
            "profile",
            "image.png",
            MediaType.IMAGE_PNG_VALUE,
            "fake-image-content".getBytes()
        );
    }

}
