package com.sprint.mission.discodeit.controller;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.User.UserCreateRequest;
import com.sprint.mission.discodeit.dto.User.UserDto;
import com.sprint.mission.discodeit.dto.User.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.UserStatus.UserStatusDto;
import com.sprint.mission.discodeit.dto.UserStatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
@ActiveProfiles("test")
@DisplayName("UserController 슬라이스 테스트")
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserStatusService userStatusService;

    @Test
    @DisplayName("유저 생성 API가 정상적으로 동작한다.")
    void shouldCreateUser_whenValidRequest() throws Exception {

        // given
        UUID userId = UUID.randomUUID();
        UserCreateRequest createRequest = new UserCreateRequest("테스트유저", "test@codeit.com",
                "test1234");

        MockMultipartFile jsonPart = new MockMultipartFile(
                "userCreateRequest",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(createRequest)
        );
        UserDto userDto = new UserDto(userId, "테스트유저", "test@codeit.com", null, true);

        given(userService.create(any(UserCreateRequest.class),
                nullable(BinaryContentCreateRequest.class))).willReturn(userDto);

        // when & then
        mockMvc.perform(multipart("/api/users")
                        .file(jsonPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.username").value("테스트유저"))
                .andExpect(jsonPath("$.email").value("test@codeit.com"));
    }

    @Test
    @DisplayName("유효하지 않은 입력으로 유저 생성 시 400 에러가 발생한다.")
    void shouldReturnBadRequest_whenUserCreateRequestIsInvalid() throws Exception {

        // given
        UserCreateRequest createRequest = new UserCreateRequest("", "test@codeit.com",
                "test1234");

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
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("유저 수정 API가 정상적으로 동작한다.")
    void shouldUpdateUser_whenValidRequest() throws Exception {

        // given
        UUID userId = UUID.randomUUID();
        UserUpdateRequest updateRequest = new UserUpdateRequest("(수정)테스트유저", "test@codeit.com",
                "test1234");

        MockMultipartFile jsonPart = new MockMultipartFile(
                "userUpdateRequest",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(updateRequest)
        );

        UserDto userDto = new UserDto(userId, "(수정)테스트유저", "test@codeit.com", null, true);

        given(userService.update(userId, updateRequest, null)).willReturn(userDto);

        // when & then
        mockMvc.perform(multipart(HttpMethod.PATCH, "/api/users/" + userId.toString())
                        .file(jsonPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.username").value("(수정)테스트유저"));
    }

    @Test
    @DisplayName("유효하지 않은 입력으로 유저 수정 시 400 에러가 발생한다.")
    void shouldReturnBadRequest_whenUserUpdateRequestIsInvalid() throws Exception {

        // given
        UUID userId = UUID.randomUUID();
        UserUpdateRequest updateRequest = new UserUpdateRequest("(수정)테스트유저", "testcodeit.com",
                "test1234");

        MockMultipartFile jsonPart = new MockMultipartFile(
                "userUpdateRequest",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(updateRequest)
        );

        // when & then
        mockMvc.perform(multipart(HttpMethod.PATCH, "/api/users/" + userId.toString())
                        .file(jsonPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("유저 삭제 API가 정상적으로 동작한다.")
    void shouldDeleteUser_whenValidUserId() throws Exception {

        // given
        UUID userId = UUID.randomUUID();

        willDoNothing().given(userService).delete(userId);

        // when & then
        mockMvc.perform(delete("/api/users/" + userId.toString()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("유저 전체조회 API가 정상적으로 동작한다.")
    void shouldReturnAllUsers_whenRequested() throws Exception {

        // given
        List<UserDto> users = List.of(
                new UserDto(UUID.randomUUID(), "테스트 유저1", "test1@codeit.com", null, true),
                new UserDto(UUID.randomUUID(), "테스트 유저2", "test2@codeit.com", null, true)
        );

        given(userService.findAll()).willReturn(users);

        // when & then
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("테스트 유저1"))
                .andExpect(jsonPath("$[0].email").value("test1@codeit.com"))
                .andExpect(jsonPath("$[1].username").value("테스트 유저2"))
                .andExpect(jsonPath("$[1].email").value("test2@codeit.com"));
    }

    @Test
    @DisplayName("유저 온라인 상태 업데이트 API가 정상적으로 동작한다.")
    void shouldUpdateUserStatus_whenValidRequest() throws Exception {

        // given
        UUID userId = UUID.randomUUID();
        UUID userStatusId = UUID.randomUUID();
        Instant now = Instant.now();

        UserStatusUpdateRequest updateRequest = new UserStatusUpdateRequest(now);
        UserStatusDto userStatusDto = new UserStatusDto(userStatusId, userId, now);

        given(userStatusService.updateByUserId(userId, updateRequest)).willReturn(userStatusDto);

        // when & then
        mockMvc.perform(patch("/api/users/" + userId.toString() + "/userStatus")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsBytes(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userStatusId.toString()))
                .andExpect(jsonPath("$.lastActiveAt").value(now.toString()));
    }
}
