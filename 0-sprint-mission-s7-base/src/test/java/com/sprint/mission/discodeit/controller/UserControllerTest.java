package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
@Import(GlobalExceptionHandler.class)
@DisplayName("유저 Controller 슬라이스 테스트")
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserStatusService userStatusService;

    @Test
    @DisplayName("POST /users - case : success")
    void createUserSuccess() throws Exception {
        UUID userId = UUID.randomUUID();

        when(userService.create(any(UserCreateRequest.class),any()))
            .thenReturn(new UserDto(userId,"김현기","test@test.com",null,true));

        MockMultipartFile userPart = new MockMultipartFile(
            "userCreateRequest",
            "",
            "application/json",
            """
                {
                  "username": "김현기",
                  "email": "test@test.com",
                  "password": "009874"
                }
                """.getBytes(StandardCharsets.UTF_8)
        );

        MockMultipartFile profilePart = new MockMultipartFile(
            "profile",
            "profile.jpg",
            MediaType.IMAGE_JPEG_VALUE,
            "profile image".getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart("/api/users")
            .file(userPart)
            .file(profilePart)
            .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(userId.toString()))
            .andExpect(jsonPath("$.username").value("김현기"))
            .andExpect(jsonPath("$.email").value("test@test.com"));
    }

    @Test
    @DisplayName("POST /users - case : 잘못된 이메일로 인한 failed")
    void createUserFail() throws Exception {
        MockMultipartFile invalidEmailRequest = new MockMultipartFile(
            "userCreateRequest",
            "",
            "application/json",
            """
                {
                  "username": "김현기",
                  "email": "test!test.com",
                  "password": "009874"
                }
                """.getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(
                multipart("/api/users")
                    .file(invalidEmailRequest)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
            )
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /users - case : success")
    void deleteUserSuccess() throws Exception {
        UUID userId = UUID.randomUUID();

        doNothing().when(userService).delete(userId);

        mockMvc.perform(delete("/api/users/{userId}",userId))
            .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /users - case : 존재하지 않는 유저로 인한 failed")
    void deleteUserNotFound() throws Exception {
        UUID userId = UUID.randomUUID();

        doThrow(new UserNotFoundException())
            .when(userService).delete(userId);

        mockMvc.perform(delete("/api/users/{userId}",userId))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("유저를 찾을 수 없습니다."));
    }
}
