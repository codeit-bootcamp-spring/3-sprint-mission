package com.sprint.mission.discodeit.slice.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.controller.UserController;
import com.sprint.mission.discodeit.dto.user.JpaUserResponse;
import com.sprint.mission.discodeit.dto.user.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.userStatus.JpaUserStatusResponse;
import com.sprint.mission.discodeit.dto.userStatus.UserStatusUpdateByUserIdRequest;
import com.sprint.mission.discodeit.exception.UserAlreadyExistsException;
import com.sprint.mission.discodeit.exception.UserNotFoundException;
import com.sprint.mission.discodeit.unit.basic.BasicUserService;
import com.sprint.mission.discodeit.unit.basic.BasicUserStatusService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.hasKey;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
/**
 * PackageName  : com.sprint.mission.discodeit.slice.controller
 * FileName     : UserControllerTest
 * Author       : dounguk
 * Date         : 2025. 6. 21.
 */
@WebMvcTest(controllers = UserController.class)
@DisplayName("User Controller 슬라이스 테스트")
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BasicUserService userService;

    @MockitoBean
    private BasicUserStatusService userStatusService;


    @Test
    @DisplayName("모든 유저를 찾는 API가 정상 작동한다.")
    void findAllUsers_success() throws Exception {
        // given
        JpaUserResponse response1 = JpaUserResponse.builder()
            .id(UUID.randomUUID())
            .username("testUser1")
            .email("test1@example.com")
            .build();
        JpaUserResponse response2 = JpaUserResponse.builder()
            .id(UUID.randomUUID())
            .username("testUser2")
            .email("test2@example.com")
            .build();
        List<JpaUserResponse> responses = List.of(response1, response2);

        given(userService.findAllUsers()).willReturn(responses);

        // when & then
        mockMvc.perform(get("/api/users")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(responses.size()))
            .andExpect(jsonPath("$[0].id").value(response1.id().toString()))
            .andExpect(jsonPath("$[0].username").value(response1.username().toString()))
            .andExpect(jsonPath("$[1].id").value(response2.id().toString()))
            .andExpect(jsonPath("$[1].username").value(response2.username().toString()));
    }

    @Test
    @DisplayName("유저가 없으면 빈 리스트를 반환한다.")
    void whenNoUsers_thenReturnEmptyList() throws Exception {
        // given
        List<JpaUserResponse> responses = Collections.emptyList();

        given(userService.findAllUsers()).willReturn(responses);

        // when n then
        mockMvc.perform(get("/api/users")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.size()").value(0));
    }


    @Test
    @DisplayName("유저 생성 API가 정상 동작 한다.")
    void createUser_success() throws Exception {
        // given
        UserCreateRequest request = new UserCreateRequest("paul","test@test.com","1234");

        MockMultipartFile jsonPart = new MockMultipartFile(
            "userCreateRequest",
            "",
            MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsBytes(request)
        );

        JpaUserResponse response = JpaUserResponse.builder()
            .username("paul")
            .email("test@test.com")
            .build();

        given(userService.create(any(UserCreateRequest.class), any())).willReturn(response);

        // when n then
        mockMvc.perform(multipart("/api/users")
                .file(jsonPart)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.username").value(request.username()))
            .andExpect(jsonPath("$.email").value(request.email()));
    }

    @Test
    @DisplayName("유저 이름이 중복일 경우 UserAlreadyExistsException(400)을 반환한다.")
    void createUser_sameName_UserAlreadyExistsException() throws Exception {
        // given
        UserCreateRequest request = new UserCreateRequest("paul","test@test.com","1234");


        MockMultipartFile jsonPart = new MockMultipartFile(
            "userCreateRequest",
            "",
            MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsBytes(request)
        );

        given(userService.create(any(UserCreateRequest.class), any()))
            .willThrow(new UserAlreadyExistsException(Map.of("username", "paul")));

        // when n then
        mockMvc.perform(multipart("/api/users")
                .file(jsonPart)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("유저가 이미 있습니다."))
            .andExpect(jsonPath("$.code").value("USER_ALREADY_EXISTS"))
            .andExpect(jsonPath("$.details").isMap())
            .andExpect(jsonPath("$.details", hasKey("username")))
            .andExpect(jsonPath("$.details.username").value("paul"));
    }

    @Test
    @DisplayName("유저 삭제 API가 정상 작동한다.")
    void deleteUser_success() throws Exception {
        // given
        UUID userId = UUID.randomUUID();

        // when n then
        mockMvc.perform(delete("/api/users/{userId}", userId))
            .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("삭제할 유저를 찾지 못할 경우 UserNotFoundException(404)를 반환한다.")
    void deleteUser_noUser_UserNotFoundException() throws Exception {
        // given
        UUID userId = UUID.randomUUID();

        doThrow(new UserNotFoundException(Map.of("userId", userId)))
            .when(userService).deleteUser(any(UUID.class));

        // when n then
        mockMvc.perform(delete("/api/users/{userId}", userId))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("유저를 찾을 수 없습니다."))
            .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"))
            .andExpect(jsonPath("$.details").isMap())
            .andExpect(jsonPath("$.details", hasKey("userId")))
            .andExpect(jsonPath("$.details.userId").value(userId.toString()));
    }

     @Test
     @DisplayName("유저 업데이트 API가 정상 작동한다.")
     void updateUser_success() throws Exception {
         // given
         UUID userId = UUID.randomUUID();
         UserUpdateRequest request = new UserUpdateRequest("daniel","daniel@test.com","");

         MockMultipartFile jsonPart = new MockMultipartFile(
             "userUpdateRequest",
             "",
             MediaType.APPLICATION_JSON_VALUE,
             objectMapper.writeValueAsBytes(request)
         );

         JpaUserResponse response = JpaUserResponse.builder()
             .username("daniel")
             .email("daniel@test.com")
             .build();

         given(userService.update(any(UUID.class), any(UserUpdateRequest.class), any()))
             .willReturn(response);

         // when n then
         mockMvc.perform(multipart("/api/users/{userId}", userId)
                 .file(jsonPart)
                 .accept(MediaType.APPLICATION_JSON)
                 .with(r-> {
                     r.setMethod("PATCH"); return r;
                 }))
             .andExpect(status().isOk())
             .andExpect(jsonPath("$.username").value("daniel"))
             .andExpect(jsonPath("$.email").value("daniel@test.com"));
     }

     @Test
     @DisplayName("업데이트할 유저를 찾지 못할경우 UserNotFoundException(404)을 반환한다.")
     void updateUser_UserNotFoundException() throws Exception {
         // given
         UUID userId = UUID.randomUUID();
         UserUpdateRequest request = new UserUpdateRequest("daniel","daniel@test.com","");

         MockMultipartFile jsonPart = new MockMultipartFile(
             "userUpdateRequest",
             "",
             MediaType.APPLICATION_JSON_VALUE,
             objectMapper.writeValueAsBytes(request)
         );

         given(userService.update(any(UUID.class), any(UserUpdateRequest.class), any()))
             .willThrow(new UserNotFoundException(Map.of("userId", userId)));

         // when n then
         mockMvc.perform(multipart("/api/users/{userId}", userId)
                 .file(jsonPart)
                 .accept(MediaType.APPLICATION_JSON)
                 .with(r-> {
                     r.setMethod("PATCH"); return r;
                 }))
             .andExpect(status().isNotFound())
             .andExpect(jsonPath("$.message").value("유저를 찾을 수 없습니다."))
             .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"))
             .andExpect(jsonPath("$.details").isMap())
             .andExpect(jsonPath("$.details", hasKey("userId")))
             .andExpect(jsonPath("$.details.userId").value(userId.toString()));
     }

     @Test
     @DisplayName("유저 최근 접속시간 업데이트 API가 정상 작동 한다.")
     void updateUserStatus_success() throws Exception {
         // given
         Instant newLastActiveAt = Instant.now();
         UUID id = UUID.randomUUID();
         UUID userId = UUID.randomUUID();
         UserStatusUpdateByUserIdRequest request = new UserStatusUpdateByUserIdRequest(newLastActiveAt);
         JpaUserStatusResponse response = new JpaUserStatusResponse(id, userId, newLastActiveAt);

         given(userStatusService.updateByUserId(any(UUID.class), any(Instant.class))).willReturn(response);

         // when n then
         mockMvc.perform(patch("/api/users/{userId}/userStatus", userId)
                 .contentType(MediaType.APPLICATION_JSON)
                 .content(objectMapper.writeValueAsBytes(request)))
             .andExpect(status().isOk())
             .andExpect(jsonPath("$.id").value(id.toString()))
             .andExpect(jsonPath("$.userId").value(userId.toString()))
             .andExpect(jsonPath("$.lastActiveAt").value(newLastActiveAt.toString()));
     }

     @Test
     @DisplayName("업데이트 유저가 없을경우 UserNotFoundException(404)를 반환한다.")
     void updateUser_noUser_UserNotFoundException() throws Exception {
         // given
         Instant newLastActiveAt = Instant.now();
         UUID userId = UUID.randomUUID();
         UserStatusUpdateByUserIdRequest request = new UserStatusUpdateByUserIdRequest(newLastActiveAt);

         given(userStatusService.updateByUserId(any(UUID.class), any(Instant.class)))
             .willThrow(new UserNotFoundException(Map.of("userId", userId)));

         // when n then
         mockMvc.perform(patch("/api/users/{userId}/userStatus", userId)
                 .contentType(MediaType.APPLICATION_JSON)
                 .content(objectMapper.writeValueAsBytes(request)))
             .andExpect(status().isNotFound())
             .andExpect(jsonPath("$.message").value("유저를 찾을 수 없습니다."))
             .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"))
             .andExpect(jsonPath("$.details").isMap())
             .andExpect(jsonPath("$.details", hasKey("userId")))
             .andExpect(jsonPath("$.details.userId").value(userId.toString()));
     }
}
