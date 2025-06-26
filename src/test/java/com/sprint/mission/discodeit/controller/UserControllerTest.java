package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserStatusService userStatusService;

    @Autowired
    private ObjectMapper objectMapper;
    private UserRepository userRepository;

    @Test
    @DisplayName("사용자 생성 API 성공")
    void createUser_success() throws Exception {
        // given
        UserCreateRequest request = new UserCreateRequest("username", "email@test.com", "password");
        MockMultipartFile jsonPart = new MockMultipartFile(
            "userCreateRequest", "", "application/json", objectMapper.writeValueAsBytes(request));

        MockMultipartFile profile = new MockMultipartFile(
            "profile", "test.jpg", "image/jpeg", "file content".getBytes());

        UserDto userDto = new UserDto(UUID.randomUUID(), "username", "email@test.com", null, null);
        Mockito.when(userService.create(request, any())).thenReturn(userDto);

        // when / then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/users")
                .file(jsonPart)
                .file(profile)
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andExpect(jsonPath("$.username").value("username"))
            .andExpect(jsonPath("$.email").value("email@test.com"));
    }

    @Test
    @DisplayName("사용자 생성 API 실패 - 유효성 검증 오류")
    void createUser_validationFail() throws Exception {
        // given
        UserCreateRequest request = new UserCreateRequest("", "invalid-email", "");
        MockMultipartFile jsonPart = new MockMultipartFile(
            "userCreateRequest", "", "application/json", objectMapper.writeValueAsBytes(request));

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/users")
                .file(jsonPart)
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @DisplayName("사용자 상태 수정 API 성공")
    void updateUserStatus_success() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        UserStatusUpdateRequest updateRequest = new UserStatusUpdateRequest(Instant.now());
        UserStatusDto statusDto = new UserStatusDto(UUID.randomUUID(), userId,
            updateRequest.newLastActiveAt());

        Mockito.when(userStatusService.updateByUserId(any(), any())).thenReturn(statusDto);

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/users/" + userId + "/userStatus")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(updateRequest)))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(jsonPath("$.userId").value(userId.toString()));
    }


    @Test
    @DisplayName("전체 사용자 조회 API 성공")
    void findAllUsers_success() throws Exception {
        // given
        UserDto user1 = new UserDto(UUID.randomUUID(), "user1", "u1@test.com", null, null);
        UserDto user2 = new UserDto(UUID.randomUUID(), "user2", "u2@test.com", null, null);

        Mockito.when(userService.findAll()).thenReturn(List.of(user1, user2));

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("사용자 삭제 성공")
    void deleteUser_success() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        doNothing().when(userService).delete(userId); //doNothing -> 정상적으로 호출되었다는 것만 검증하고 싶을 때 사용

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/{userId}", userId))
            .andExpect(MockMvcResultMatchers.status().isNoContent());

        // verify
        verify(userService).delete(userId);
    }
}