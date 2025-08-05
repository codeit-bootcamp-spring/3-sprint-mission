package com.sprint.mission.discodeit.Contoller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.controller.UserController;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(UserController.class)
public class UserControllerTest {

  @Autowired
  MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;


  @MockitoBean
  UserService userService;
  @MockitoBean
  UserStatusService userStatusService;

  @Nested
  @DisplayName("유저 생성 테스트")
  class CreateUser {

    @Test
    @DisplayName("유효한 요청으로 유저 생성 성공")
    void whenCreateUser_thenReturnCreatedUser() throws Exception {
      // Given
      UserCreateRequest userCreateRequest = new UserCreateRequest("test", "test@mail.com",
          "pw1234");
      UserDto createdDto = new UserDto(UUID.randomUUID(), userCreateRequest.username(),
          userCreateRequest.email(), null,
          true, Instant.now(), Instant.now());
      given(userService.create(userCreateRequest, Optional.empty())).willReturn(createdDto);
      MockMultipartFile userPart = new MockMultipartFile(
          "userCreateRequest",            // @RequestPart 이름과 정확히 일치해야 함
          null,
          MediaType.APPLICATION_JSON_VALUE,
          objectMapper.writeValueAsBytes(userCreateRequest)
      );

      // When
      ResultActions result = mockMvc.perform(multipart("/api/users")
          .file(userPart)
          .contentType(MediaType.MULTIPART_FORM_DATA));

      // Then
      result.andExpect(status().isCreated())
          .andExpect(jsonPath("$.username").value("test"))
          .andExpect(jsonPath("$.email").value("test@mail.com"));
    }

    @Test
    @DisplayName("유저 생성 중 예외 발생 시 500 반환")
    void whenCreateUserFails_thenReturnError() throws Exception {
      // Given
      UserCreateRequest userCreateRequest = new UserCreateRequest("fail", "fail@mail.com",
          "pw1234");
      given(userService.create(userCreateRequest, Optional.empty())).willThrow(
          new RuntimeException("error"));
      MockMultipartFile userPart = new MockMultipartFile(
          "userCreateRequest",            // @RequestPart 이름과 정확히 일치해야 함
          null,
          MediaType.APPLICATION_JSON_VALUE,
          objectMapper.writeValueAsBytes(userCreateRequest)
      );

      //When
      ResultActions result = mockMvc.perform(multipart("/api/users")
          .file(userPart)
          .contentType(MediaType.MULTIPART_FORM_DATA));

      //Then
      result.andExpect(status().isInternalServerError());
    }
  }

  @Nested
  @DisplayName("유저 수정 테스트")
  class UpdateUser {

    @Test
    @DisplayName("유효한 요청으로 유저 수정 성공")
    void whenUpdateUser_thenReturnUpdatedUser() throws Exception {
      // Given
      UUID userId = UUID.randomUUID();
      UserUpdateRequest updateRequest = new UserUpdateRequest("updated", "update@mail.com",
          "newpass");
      UserDto updatedDto = new UserDto(
          userId,
          updateRequest.newUsername(),
          updateRequest.newEmail(),
          null,
          true,
          Instant.now(),
          Instant.now()
      );
      given(userService.update(userId, updateRequest, Optional.empty()))
          .willReturn(updatedDto);
      MockMultipartFile requestPart = new MockMultipartFile(
          "userUpdateRequest",
          null,
          MediaType.APPLICATION_JSON_VALUE,
          objectMapper.writeValueAsBytes(updateRequest)
      );

      //When
      ResultActions result = mockMvc.perform(multipart("/api/users/{userId}", userId)
          .file(requestPart)
          .with(req -> {
            req.setMethod("PATCH");
            return req;
          })
          .contentType(MediaType.MULTIPART_FORM_DATA));

      //Then
      result
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.username").value("updated"))
          .andExpect(jsonPath("$.email").value("update@mail.com"));
    }

    @Test
    @DisplayName("유저 수정 중 예외 발생 시 500 반환")
    void whenUpdateUserFails_thenReturnError() throws Exception {
      // Given
      UUID userId = UUID.randomUUID();
      UserUpdateRequest updateRequest = new UserUpdateRequest("fail", "fail@mail.com", "failpass");
      given(userService.update(userId, updateRequest, Optional.empty()))
          .willThrow(new RuntimeException("error"));
      MockMultipartFile requestPart = new MockMultipartFile(
          "userUpdateRequest",
          null,
          MediaType.APPLICATION_JSON_VALUE,
          objectMapper.writeValueAsBytes(updateRequest)
      );

      // When
      ResultActions result = mockMvc.perform(multipart("/api/users/{userId}", userId)
          .file(requestPart)
          .with(req -> {
            req.setMethod("PATCH");
            return req;
          })
          .contentType(MediaType.MULTIPART_FORM_DATA));

      // Then
      result.andExpect(status().isInternalServerError());
    }
  }

  @DisplayName("저장된 모든 유저리스트 반환")
  @Test
  void getAllUser_returnsUserDtoLists() throws Exception {
    //Given
    UUID userIdOne = UUID.randomUUID();
    UUID userIdTwo = UUID.randomUUID();
    UserDto userOneDto = new UserDto(userIdOne, "one",
        "one@email.com",
        null, true,
        Instant.now(), Instant.now());
    UserDto userTwoDto = new UserDto(userIdTwo, "two",
        "two@email.com",
        null, true,
        Instant.now(), Instant.now());
    given(userService.findAll()).willReturn(List.of(userOneDto, userTwoDto));

    // When
    ResultActions result = mockMvc.perform(get("/api/users"));

    // Then
    result
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(2))
        .andExpect(jsonPath("$[0].username").value("one"))
        .andExpect(jsonPath("$[1].email").value("two@email.com"));
  }

}
