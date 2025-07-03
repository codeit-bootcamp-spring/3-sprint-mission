package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusNotFoundException;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import com.sprint.mission.discodeit.testutil.TestDataBuilder;
import java.lang.reflect.Field;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
@DisplayName("UserController 슬라이스 테스트")
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
  @DisplayName("사용자 생성 - 성공 (프로필 파일 없음)")
  void create_Success_WithoutProfile() throws Exception {
    // given
    UserCreateRequest request = TestDataBuilder.createDefaultUserCreateRequest();
    UserDto responseDto = TestDataBuilder.createDefaultUserDto();

    when(userService.create(eq(request), eq(Optional.empty()))).thenReturn(responseDto);

    MockMultipartFile requestPart = new MockMultipartFile(
        "userCreateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(request));

    // when & then
    mockMvc.perform(multipart("/api/users")
        .file(requestPart)
        .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(responseDto.id().toString()))
        .andExpect(jsonPath("$.username").value(responseDto.username()))
        .andExpect(jsonPath("$.email").value(responseDto.email()));
  }

  @Test
  @DisplayName("사용자 생성 - 성공 (프로필 파일 포함)")
  void create_Success_WithProfile() throws Exception {
    // given
    UserCreateRequest request = TestDataBuilder.createDefaultUserCreateRequest();
    UserDto responseDto = TestDataBuilder.createDefaultUserDto();

    when(userService.create(eq(request), any())).thenReturn(responseDto);

    MockMultipartFile requestPart = new MockMultipartFile(
        "userCreateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(request));

    MockMultipartFile profileFile = new MockMultipartFile(
        "profile",
        "profile.jpg",
        MediaType.IMAGE_JPEG_VALUE,
        "test image content".getBytes());

    // when & then
    mockMvc.perform(multipart("/api/users")
        .file(requestPart)
        .file(profileFile)
        .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(responseDto.id().toString()))
        .andExpect(jsonPath("$.username").value(responseDto.username()))
        .andExpect(jsonPath("$.email").value(responseDto.email()));
  }

  @Test
  @DisplayName("사용자 생성 - 실패 (유효하지 않은 요청)")
  void create_Fail_InvalidRequest() throws Exception {
    // given
    UserCreateRequest invalidRequest = new UserCreateRequest("", "", "");

    MockMultipartFile requestPart = new MockMultipartFile(
        "userCreateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(invalidRequest));

    // when & then
    mockMvc.perform(multipart("/api/users")
        .file(requestPart)
        .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("사용자 수정 - 성공 (프로필 파일 없음)")
  void update_Success_WithoutProfile() throws Exception {
    // given
    UUID userId = TestDataBuilder.USER_ID_1;
    UserUpdateRequest request = TestDataBuilder.createUserUpdateRequest("newUser", "new@example.com");
    UserDto responseDto = TestDataBuilder.createUserDto(userId, "newUser", "new@example.com");

    when(userService.update(eq(userId), eq(request), eq(Optional.empty()))).thenReturn(responseDto);

    MockMultipartFile requestPart = new MockMultipartFile(
        "userUpdateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(request));

    // when & then
    mockMvc.perform(multipart("/api/users/{userId}", userId)
        .file(requestPart)
        .with(request1 -> {
          request1.setMethod("PATCH");
          return request1;
        })
        .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(responseDto.id().toString()))
        .andExpect(jsonPath("$.username").value(responseDto.username()))
        .andExpect(jsonPath("$.email").value(responseDto.email()));
  }

  @Test
  @DisplayName("사용자 수정 - 실패 (존재하지 않는 사용자)")
  void update_Fail_UserNotFound() throws Exception {
    // given
    UUID userId = UUID.randomUUID();
    UserUpdateRequest request = TestDataBuilder.createUserUpdateRequest("newUser", "new@example.com");

    when(userService.update(eq(userId), eq(request), any())).thenThrow(UserNotFoundException.withUserId(userId));

    MockMultipartFile requestPart = new MockMultipartFile(
        "userUpdateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(request));

    // when & then
    mockMvc.perform(multipart("/api/users/{userId}", userId)
        .file(requestPart)
        .with(request1 -> {
          request1.setMethod("PATCH");
          return request1;
        })
        .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("사용자 삭제 - 성공")
  void delete_Success() throws Exception {
    // given
    UUID userId = TestDataBuilder.USER_ID_1;
    doNothing().when(userService).delete(userId);

    // when & then
    mockMvc.perform(delete("/api/users/{userId}", userId))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("사용자 삭제 - 실패 (존재하지 않는 사용자)")
  void delete_Fail_UserNotFound() throws Exception {
    // given
    UUID userId = UUID.randomUUID();
    doThrow(UserNotFoundException.withUserId(userId)).when(userService).delete(userId);

    // when & then
    mockMvc.perform(delete("/api/users/{userId}", userId))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("전체 사용자 조회 - 성공")
  void findAll_Success() throws Exception {
    // given
    List<UserDto> users = TestDataBuilder.createUserDtoList();
    when(userService.findAll()).thenReturn(users);

    // when & then
    mockMvc.perform(get("/api/users"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(users.size()))
        .andExpect(jsonPath("$[0].id").value(users.get(0).id().toString()))
        .andExpect(jsonPath("$[0].username").value(users.get(0).username()))
        .andExpect(jsonPath("$[1].id").value(users.get(1).id().toString()))
        .andExpect(jsonPath("$[1].username").value(users.get(1).username()));
  }

  @Test
  @DisplayName("사용자 상태 수정 - 성공")
  void updateUserStatusByUserId_Success() throws Exception {
    // given
    UUID userId = TestDataBuilder.USER_ID_1;
    Instant lastActiveAt = Instant.now();
    UserStatusUpdateRequest request = new UserStatusUpdateRequest(lastActiveAt);

    // User를 ID와 함께 생성
    User user = TestDataBuilder.createDefaultUser();
    try {
      Field idField = User.class.getSuperclass().getSuperclass().getDeclaredField("id");
      idField.setAccessible(true);
      idField.set(user, userId);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    UserStatus userStatus = new UserStatus(user, lastActiveAt);

    when(userStatusService.updateByUserId(userId, request)).thenReturn(userStatus);

    // when & then
    mockMvc.perform(patch("/api/users/{userId}/userStatus", userId)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.lastActiveAt").exists());
  }

  @Test
  @DisplayName("사용자 상태 수정 - 실패 (존재하지 않는 사용자)")
  void updateUserStatusByUserId_Fail_UserNotFound() throws Exception {
    // given
    UUID userId = UUID.randomUUID();
    Instant lastActiveAt = Instant.now();
    UserStatusUpdateRequest request = new UserStatusUpdateRequest(lastActiveAt);

    when(userStatusService.updateByUserId(userId, request))
        .thenThrow(UserStatusNotFoundException.withUserId(userId));

    // when & then
    mockMvc.perform(patch("/api/users/{userId}/userStatus", userId)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("사용자 상태 수정 - 실패 (유효하지 않은 요청)")
  void updateUserStatusByUserId_Fail_InvalidRequest() throws Exception {
    // given
    UUID userId = TestDataBuilder.USER_ID_1;
    UserStatusUpdateRequest invalidRequest = new UserStatusUpdateRequest(null);

    // when & then
    mockMvc.perform(patch("/api/users/{userId}/userStatus", userId)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest());
  }
}
