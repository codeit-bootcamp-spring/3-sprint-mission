package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.exception.user.DuplicateUsernameException;
import com.sprint.mission.discodeit.service.UserService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
@Import(GlobalExceptionHandler.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean(name = "jpaMappingContext")
    Object dummyJpaMappingContext;

    @MockitoBean(name = "jpaAuditingHandler")
    Object dummyJpaAuditingHandler;

    @Test
    @DisplayName("사용자 생성 성공")
    @WithMockUser(username = "tester", roles = {"USER"})
    void shouldCreateUserSuccessfully() throws Exception {
        UserCreateRequest request = new UserCreateRequest("tester", "tester@email.com", "password");
        MockMultipartFile userPart = new MockMultipartFile("userCreateRequest", null,
            "application/json", objectMapper.writeValueAsBytes(request));
        MockMultipartFile profile = new MockMultipartFile("profile", "profile.png", "image/png",
            "dummy".getBytes());

        UserResponse response = new UserResponse(UUID.randomUUID(), "tester", "tester@email.com",
            null, true, Role.USER);
        BDDMockito.given(userService.create(any(), any())).willReturn(response);

        mockMvc.perform(multipart("/api/users").file(userPart).file(profile).with(csrf())
                .contentType(MediaType.MULTIPART_FORM_DATA)).andExpect(status().isCreated())
            .andExpect(jsonPath("$.username").value("tester"))
            .andExpect(jsonPath("$.email").value("tester@email.com"))
            .andExpect(jsonPath("$.online").value(true));
    }

    @Test
    @DisplayName("사용자 생성 실패 - 중복 사용자 이름")
    @WithMockUser(username = "tester", roles = {"USER"})
    void shouldFailToCreateUserWithDuplicateUsername() throws Exception {
        UserCreateRequest request = new UserCreateRequest("tester", "dup@email.com", "password");
        MockMultipartFile userPart = new MockMultipartFile("userCreateRequest", null,
            "application/json", objectMapper.writeValueAsBytes(request));

        BDDMockito.given(userService.create(any(), any()))
            .willThrow(new DuplicateUsernameException("tester"));

        mockMvc.perform(multipart("/api/users").file(userPart).with(csrf())
                .contentType(MediaType.MULTIPART_FORM_DATA)).andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value("DUPLICATE_USERNAME"))
            .andExpect(jsonPath("$.exceptionType").value("DuplicateUsernameException"))
            .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    @DisplayName("모든 사용자 조회 성공")
    @WithMockUser(username = "tester", roles = {"USER"})
    void shouldFindAllUsersSuccessfully() throws Exception {
        List<UserResponse> users = List.of(
            new UserResponse(UUID.randomUUID(), "user1", "user1@email.com", null, true, Role.USER),
            new UserResponse(UUID.randomUUID(), "user2", "user2@email.com", null, true, Role.USER));

        BDDMockito.given(userService.findAll()).willReturn(users);

        mockMvc.perform(get("/api/users").with(csrf())).andExpect(status().isOk())
            .andExpect(jsonPath("$.size()").value(2))
            .andExpect(jsonPath("$[0].username").value("user1"))
            .andExpect(jsonPath("$[1].username").value("user2"));
    }

    @Test
    @DisplayName("사용자 삭제 성공")
    @WithMockUser(username = "tester", roles = {"USER"})
    void shouldDeleteUserSuccessfully() throws Exception {
        UUID userId = UUID.randomUUID();
        UserResponse response = new UserResponse(userId, "tester", "tester@email.com", null, true,
            Role.USER);

        BDDMockito.given(userService.delete(userId)).willReturn(response);

        mockMvc.perform(delete("/api/users/{userId}", userId).with(csrf()))
            .andExpect(status().isNoContent());
    }
}
