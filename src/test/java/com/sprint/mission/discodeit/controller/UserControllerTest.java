package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;

@WebMvcTest(UserController.class)
@Import({ UserService.class, UserStatusService.class })
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    UserService userService;
    @MockitoBean
    UserStatusService userStatusService;

    @Test
    void 전체_사용자_목록_조회() throws Exception {
        UserResponse user1 = new UserResponse(UUID.randomUUID(), "user1", "user1@test.com", null, true);
        UserResponse user2 = new UserResponse(UUID.randomUUID(), "user2", "user2@test.com", null,
                false);
        when(userService.findAll()).thenReturn(List.of(user1, user2));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("user1@test.com"))
                .andExpect(jsonPath("$[1].email").value("user2@test.com"));
    }

    @Test
    void 이메일로_사용자_조회_존재하지_않음() throws Exception {
        when(userService.findByEmail(any())).thenThrow(new RuntimeException("User not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/email")
                .param("email", "notfound@test.com"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void 사용자_생성_성공() throws Exception {
        // given
        UserCreateRequest request = new UserCreateRequest("test@test.com", "tester", "password123");
        String requestJson = objectMapper.writeValueAsString(request);
        MockMultipartFile userCreateRequest = new MockMultipartFile(
                "userCreateRequest",
                null,
                "application/json",
                requestJson.getBytes());
        MockMultipartFile profile = new MockMultipartFile(
                "profile",
                "profile.jpg",
                "image/jpeg",
                new byte[0]);
        UserResponse expected = new UserResponse(
                java.util.UUID.randomUUID(),
                "tester",
                "test@test.com",
                null,
                true);
        when(userService.create(any())).thenReturn(expected);

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/users")
                .file(userCreateRequest)
                .file(profile)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("tester"))
                .andExpect(jsonPath("$.email").value("test@test.com"));
    }
}
