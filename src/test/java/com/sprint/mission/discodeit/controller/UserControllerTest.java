package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
@ActiveProfiles("test")
@DisplayName("UserController 테스트")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserStatusService userStatusService;

    @Test
    @DisplayName("사용자 생성 - 성공")
    void createUser_Success() throws Exception {
        // given
        UserCreateRequest request = new UserCreateRequest(
            "testuser",
            "test@example.com",
            "password123"
        );

        UserDto responseDto = new UserDto(
            UUID.randomUUID(),
            "testuser",
            "test@example.com",
            null, // profile
            true // online
        );

        given(userService.create(any(UserCreateRequest.class), any(Optional.class)))
            .willReturn(responseDto);

        MockMultipartFile requestPart = new MockMultipartFile(
            "userCreateRequest",
            "",
            MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsBytes(request)
        );

        // when & then
        mockMvc.perform(multipart("/api/users")
                .file(requestPart)
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.username").value("testuser"))
            .andExpect(jsonPath("$.email").value("test@example.com"))
            .andExpect(jsonPath("$.online").value(true));
    }

    @Test
    @DisplayName("사용자 생성 - 실패 (유효성 검증 오류)")
    void createUser_ValidationFailure() throws Exception {
        // given
        UserCreateRequest invalidRequest = new UserCreateRequest(
            "",
            "invalid-email",
            "123"
        );

        MockMultipartFile requestPart = new MockMultipartFile(
            "userCreateRequest",
            "",
            MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsBytes(invalidRequest)
        );

        // when & then
        mockMvc.perform(multipart("/api/users")
                .file(requestPart)
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("사용자 수정 - 성공")
    void updateUser_Success() throws Exception {
        // given
        UUID userId = UUID.randomUUID();

        UserUpdateRequest request = new UserUpdateRequest(
            "updateduser",
            "updated@example.com",
            "updatedpassword123"
        );

        UserDto responseDto = new UserDto(
            userId,
            "updateduser",
            "updated@example.com",
            null,
            true
        );

        given(userService.update(eq(userId), eq(request), any(Optional.class)))
            .willReturn(responseDto);

        MockMultipartFile requestPart = new MockMultipartFile(
            "userUpdateRequest",
            "",
            MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsBytes(request)
        );

        // when & then
        mockMvc.perform(multipart("/api/users/{userId}", userId)
                .file(requestPart)
                .with(req -> {
                    req.setMethod("PATCH");
                    return req;
                })
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(userId.toString()))
            .andExpect(jsonPath("$.username").value("updateduser"))
            .andExpect(jsonPath("$.email").value("updated@example.com"));
    }

    @Test
    @DisplayName("사용자 상태 수정 - 실패 (유효성 검증 오류)")
    void updateUserStatus_ValidationFailure() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        UserStatusUpdateRequest invalidRequest = new UserStatusUpdateRequest(null);

        // when & then
        mockMvc.perform(patch("/api/users/{userId}/userStatus", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest());
    }
}