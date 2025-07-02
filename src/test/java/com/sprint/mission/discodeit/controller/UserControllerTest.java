package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserStatusService userStatusService;

    private UUID userId;
    private BinaryContentDto binaryContentDto;
    private UserDto userDto;
    private UserCreateRequest userCreateRequest;
    private UserUpdateRequest userUpdateRequest;
    private MockMultipartFile profileFile;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        binaryContentDto = new BinaryContentDto(UUID.randomUUID(), "이미지", 1024L,
            MediaType.IMAGE_JPEG_VALUE);

        userDto = new UserDto(userId, "testuser", "test@abc.com", binaryContentDto, true);
        userCreateRequest = new UserCreateRequest("testuser", "test@abc.com", "1234");

        userUpdateRequest = new UserUpdateRequest("updateduser", "update@abc.com",
            "12345");

        profileFile = new MockMultipartFile(
            "이미지", "이미지.jpg", MediaType.IMAGE_JPEG_VALUE, "이미지".getBytes()
        );
    }

    private MockMultipartFile toMultipartJson(String name, Object value) throws Exception {
        return new MockMultipartFile(
            name,
            "",
            MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsBytes(value)
        );
    }

    @Test
    @DisplayName("사용자 생성 성공")
    void createUser_Success() throws Exception {
        given(userService.createUser(any(UserCreateRequest.class), any(Optional.class))).willReturn(
            userDto);

        mockMvc.perform(multipart("/api/users")
                .file(toMultipartJson("userCreateRequest", userCreateRequest))
                .file(profileFile)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(userId.toString()))
            .andExpect(jsonPath("$.username").value("testuser"))
            .andExpect(jsonPath("$.profile.fileName").value("이미지"));
    }

    @Test
    @DisplayName("사용자 생성 실패")
    void createUser_Failure() throws Exception {
        UserCreateRequest invalidRequest = new UserCreateRequest("a", "b", "c");

        mockMvc.perform(multipart("/api/users")
                .file(toMultipartJson("userCreateRequest", invalidRequest))
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("사용자 전체 조회 성공")
    void findAllUsers_Success() throws Exception {
        UUID userId = UUID.randomUUID();
        UserDto userDto1 = new UserDto(
            userId,
            "user1",
            "user1@abc.com",
            null,
            true
        );
        List<UserDto> users = List.of(userDto, userDto1);

        given(userService.findAll()).willReturn(users);

        mockMvc.perform(get("/api/users")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(userDto.id().toString()))
            .andExpect(jsonPath("$[0].username").value("testuser"))
            .andExpect(jsonPath("$[0].isOnline").value(true))
            .andExpect(jsonPath("$[1].id").value(userDto1.id().toString()))
            .andExpect(jsonPath("$[1].username").value("user1"))
            .andExpect(jsonPath("$[1].isOnline").value(true));
    }

    @Test
    @DisplayName("사용자 수정 성공")
    void updateUser_Success() throws Exception {
        UserDto updatedUser = new UserDto(
            userId,
            "updateduser",
            "update@abc.com",
            binaryContentDto,
            true
        );
        given(userService.update(eq(userId), any(UserUpdateRequest.class),
            any(Optional.class))).willReturn(updatedUser);

        mockMvc.perform(multipart("/api/users/{userId}", userId)
                .file(toMultipartJson("userUpdateRequest", userUpdateRequest))
                .file(profileFile)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .with(req -> {
                    req.setMethod("PATCH");
                    return req;
                }))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(userId.toString()))
            .andExpect(jsonPath("$.username").value("updateduser"))
            .andExpect(jsonPath("$.email").value("update@abc.com"))
            .andExpect(jsonPath("$.profile.fileName").value("이미지"))
            .andExpect(jsonPath("$.isOnline").value(true));
    }

    @Test
    @DisplayName("사용자 수정 실패")
    void updateUser_Failure() throws Exception {
        UUID nonExistentId = UUID.randomUUID();

        given(userService.update(eq(nonExistentId), any(UserUpdateRequest.class),
            any(Optional.class)))
            .willThrow(UserNotFoundException.withId(nonExistentId));

        mockMvc.perform(multipart("/api/users/{userId}", nonExistentId)
                .file(toMultipartJson("userUpdateRequest", userUpdateRequest))
                .file(profileFile)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .with(req -> {
                    req.setMethod("PATCH");
                    return req;
                }))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("사용자 삭제 성공")
    void deleteUser_Success() throws Exception {
        willDoNothing().given(userService).delete(userId);

        mockMvc.perform(delete("/api/users/{userId}", userId))
            .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("사용자 삭제 실패")
    void deleteUser_Failure() throws Exception {
        UUID nonExistentId = UUID.randomUUID();
        willThrow(UserNotFoundException.withId(nonExistentId)).given(userService)
            .delete(nonExistentId);

        mockMvc.perform(delete("/api/users/{userId}", nonExistentId))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("사용자 상태 수정 성공")
    void updateUserStatus_Success() throws Exception {
        Instant now = Instant.now();
        UserStatusDto statusDto = new UserStatusDto(UUID.randomUUID(), userId, now);
        UserStatusUpdateRequest request = new UserStatusUpdateRequest(now);

        given(userStatusService.updateByUserId(eq(userId),
            any(UserStatusUpdateRequest.class))).willReturn(statusDto);

        mockMvc.perform(patch("/api/users/{userId}/userStatus", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(statusDto.id().toString()))
            .andExpect(jsonPath("$.userId").value(userId.toString()));
    }

    @Test
    @DisplayName("사용자 상태 수정 실패 - 사용자 없음")
    void updateUserStatus_Failure() throws Exception {
        Instant now = Instant.now();
        UUID nonExistentId = UUID.randomUUID();
        UserStatusUpdateRequest request = new UserStatusUpdateRequest(now);

        given(userStatusService.updateByUserId(eq(nonExistentId), any()))
            .willThrow(UserNotFoundException.withId(nonExistentId));

        mockMvc.perform(patch("/api/users/{userId}/userStatus", nonExistentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }
}