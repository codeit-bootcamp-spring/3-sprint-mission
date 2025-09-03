package com.sprint.mission.discodeit.integration;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@WithMockUser(username = "tester", roles = {"USER"})
class UserIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    static class TestPrincipal {

        private final UUID id;

        TestPrincipal(UUID id) {
            this.id = id;
        }

        public Object getUserResponse() {
            return new Object() {
                public UUID id() {
                    return id;
                }
            };
        }
    }

    private Authentication ownerAuth(UUID userId) {
        return new UsernamePasswordAuthenticationToken(
            new TestPrincipal(userId),
            "N/A",
            java.util.List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    private String createUser(String username, String email) throws Exception {
        UserCreateRequest request = new UserCreateRequest(username, email, "password");
        MockMultipartFile json = new MockMultipartFile(
            "userCreateRequest",
            "",
            MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsBytes(request)
        );

        MvcResult result = mockMvc.perform(
                multipart("/api/users")
                    .file(json)
                    .with(csrf())
            )
            .andExpect(status().isCreated())
            .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asText();
    }

    @Test
    @DisplayName("프로필 이미지 포함 사용자 생성 성공")
    void shouldCreateUserWithProfileImageSuccessfully() throws Exception {
        UserCreateRequest createRequest = new UserCreateRequest(
            "testuser",
            "test@example.com",
            "Password"
        );

        MockMultipartFile userCreateRequest = new MockMultipartFile(
            "userCreateRequest",
            "",
            MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsBytes(createRequest)
        );

        MockMultipartFile profile = new MockMultipartFile(
            "profile",
            "profile.jpg",
            MediaType.IMAGE_JPEG_VALUE,
            "이미지_내용".getBytes()
        );

        mockMvc.perform(
                multipart("/api/users")
                    .file(userCreateRequest)
                    .file(profile)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .with(csrf())
            )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.username", is("testuser")))
            .andExpect(jsonPath("$.email", is("test@example.com")))
            .andExpect(jsonPath("$.profile.fileName", is("profile.jpg")))
            .andExpect(jsonPath("$.profile.contentType", is("image/jpeg")));
    }

    @Test
    @DisplayName("프로필 이미지 제외 사용자 생성 성공")
    void shouldCreateUserWithEmptyProfileImageSuccessfully() throws Exception {
        UserCreateRequest request = new UserCreateRequest("emptyfile", "empty@email.com",
            "password");

        MockMultipartFile jsonPart = new MockMultipartFile(
            "userCreateRequest",
            "",
            MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsBytes(request)
        );

        MockMultipartFile emptyProfile = new MockMultipartFile(
            "profile",
            "empty.jpg",
            MediaType.IMAGE_JPEG_VALUE,
            new byte[0]
        );

        mockMvc.perform(
                multipart("/api/users")
                    .file(jsonPart)
                    .file(emptyProfile)
                    .with(csrf())
            )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.username", is("emptyfile")))
            .andExpect(jsonPath("$.profile").doesNotExist());
    }

    @Test
    @DisplayName("사용자 생성 실패 - 유효하지 않은 입력")
    void shouldFailToCreateUserWithInvalidInput() throws Exception {
        UserCreateRequest request = new UserCreateRequest("", "invalid", "");
        MockMultipartFile json = new MockMultipartFile(
            "userCreateRequest",
            "",
            MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsBytes(request)
        );

        mockMvc.perform(multipart("/api/users").file(json).with(csrf()))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("사용자 목록 조회 성공")
    void shouldGetUserListSuccessfully() throws Exception {
        createUser("user1", "u1@email.com");
        createUser("user2", "u2@email.com");

        mockMvc.perform(get("/api/users"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("user1")))
            .andExpect(content().string(containsString("user2")));
    }

    @Test
    @DisplayName("사용자 수정 성공")
    void shouldUpdateUserSuccessfully() throws Exception {
        String userId = createUser("before", "before@email.com");

        String patchJson = """
            {
              "newUsername": "after",
              "newEmail": "after@email.com",
              "newPassword": "newpass123"
            }
            """;

        MockMultipartFile patchFile = new MockMultipartFile(
            "userUpdateRequest",
            "",
            MediaType.APPLICATION_JSON_VALUE,
            patchJson.getBytes()
        );

        mockMvc.perform(
                multipart(HttpMethod.PATCH, "/api/users/{id}", userId)
                    .file(patchFile)
                    .with(csrf())
                    .with(authentication(ownerAuth(UUID.fromString(userId))))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username", is("after")))
            .andExpect(jsonPath("$.email", is("after@email.com")));
    }

    @Test
    @DisplayName("사용자 수정 실패 - 존재하지 않는 사용자")
    void shouldFailToUpdateUserWhenNotFound() throws Exception {
        String fakeId = UUID.randomUUID().toString();

        String patchJson = """
            {
              "newUsername": "after",
              "newEmail": "after@email.com",
              "newPassword": "newpass123"
            }
            """;

        MockMultipartFile patchFile = new MockMultipartFile(
            "userUpdateRequest",
            "",
            MediaType.APPLICATION_JSON_VALUE,
            patchJson.getBytes()
        );

        mockMvc.perform(
                multipart(HttpMethod.PATCH, "/api/users/{id}", fakeId)
                    .file(patchFile)
                    .with(csrf())
                    .with(authentication(ownerAuth(UUID.fromString(fakeId))))
            )
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("사용자 삭제 성공")
    void shouldDeleteUserSuccessfully() throws Exception {
        String userId = createUser("deleteme", "delete@email.com");

        mockMvc.perform(
                delete("/api/users/{id}", UUID.fromString(userId))
                    .with(csrf())
                    .with(authentication(ownerAuth(UUID.fromString(userId))))
            )
            .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("사용자 삭제 실패 - 존재하지 않는 사용자")
    void shouldFailToDeleteUserWhenNotFound() throws Exception {
        UUID fakeId = UUID.randomUUID();

        mockMvc.perform(
                delete("/api/users/{id}", fakeId)
                    .with(csrf())
                    .with(authentication(ownerAuth(fakeId)))
            )
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("중복 사용자 생성 실패")
    void shouldFailToCreateUserWithDuplicateUsername() throws Exception {
        createUser("duplicate", "dup@email.com");

        UserCreateRequest request = new UserCreateRequest("duplicate", "dup@email.com", "password");
        MockMultipartFile json = new MockMultipartFile(
            "userCreateRequest",
            "",
            MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsBytes(request)
        );

        mockMvc.perform(multipart("/api/users").file(json).with(csrf()))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.code", is("DUPLICATE_BOTH")));
    }
}
