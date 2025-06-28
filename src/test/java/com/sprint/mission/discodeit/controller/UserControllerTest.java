package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.exception.user.EmailAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNameAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@WebMvcTest(UserController.class)
@Import({GlobalExceptionHandler.class})
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserStatusService userStatusService;

    @BeforeEach
    @DisplayName("테스트 환경 설정 확인")
    void setUp() {

        assert mockMvc != null;
        assert objectMapper != null;
        assert userService != null;
        assert userStatusService != null;
    }

    @Nested
    @DisplayName("사용자 생성 테스트")
    class CreateUserTests {

        @Test
        @DisplayName("사용자 생성 성공")
        void createUser_Success() throws Exception {
            // given
            UserCreateRequest userCreateRequest = new UserCreateRequest("테스트", "test@gmail.com",
                "!password123");
            UserDto userDto = new UserDto(UUID.randomUUID(), "테스트", "test@gmail.com", null, null);

            given(userService.create(eq(userCreateRequest), any())).willReturn(userDto);

            MockMultipartFile userCreateRequestFile
                = toJsonPart("userCreateRequest", userCreateRequest);

            MockMultipartFile profilePart = fakeImagePart();

            // When & Then
            mockMvc.perform(multipart(HttpMethod.POST, "/api/users")
                    .file(userCreateRequestFile)
                    .file(profilePart)
                    .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("테스트"))
                .andExpect(jsonPath("$.email").value("test@gmail.com"));
        }

        @Test
        @DisplayName("사용자 생성 실패 - 중복된 이메일")
        void createUser_Fail_DuplicateEmail() throws Exception {
            // Given
            UserCreateRequest userCreateRequest = new UserCreateRequest("테스트", "test@gmail.com",
                "!password123");

            given(userService.create(eq(userCreateRequest), any()))
                .willThrow(new EmailAlreadyExistsException("test@gmail.com"));

            MockMultipartFile userCreateRequestFile
                = toJsonPart("userCreateRequest", userCreateRequest);

            // When & Then
            mockMvc.perform(multipart(HttpMethod.POST, "/api/users")
                    .file(userCreateRequestFile)
                    .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("이미 등록된 Email입니다."))
                .andExpect(jsonPath("$.details.email").value("test@gmail.com"));
        }

        @Test
        @DisplayName("사용자 생성 실패 - 중복된 사용자명")
        void createUser_Fail_DuplicateUsername() throws Exception {
            // Given
            UserCreateRequest userCreateRequest = new UserCreateRequest("테스트", "test@gmail.com",
                "!password123");

            given(userService.create(eq(userCreateRequest), any()))
                .willThrow(new UserNameAlreadyExistsException("테스트"));

            MockMultipartFile userCreateRequestFile
                = toJsonPart("userCreateRequest", userCreateRequest);

            // When & Then
            mockMvc.perform(multipart(HttpMethod.POST, "/api/users")
                    .file(userCreateRequestFile)
                    .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("이미 사용 중인 UserName입니다."))
                .andExpect(jsonPath("$.details.username").value("테스트"));
        }

    }

    @Nested
    @DisplayName("사용자 수정 테스트")
    class UpdateUserTests {

        @Test
        @DisplayName("사용자 수정 성공 - 프로필 포함")
        void updateUser_Success_WithProfile() throws Exception {
            // Given
            UUID userId = UUID.randomUUID();
            UserUpdateRequest userUpdateRequest
                = new UserUpdateRequest("수정테스트", "newemail@example.com", "!password123!");

            UserDto updatedUser = new UserDto(userId, "수정테스트", "newemail@example.com", null,
                null);

            given(userService.update(eq(userId), eq(userUpdateRequest), any()))
                .willReturn(updatedUser);

            MockMultipartFile userUpdateRequestFile
                = toJsonPart("userUpdateRequest", userUpdateRequest);

            MockMultipartFile profilePart = fakeImagePart();

            // When & Then
            mockMvc.perform(multipart(HttpMethod.PATCH, "/api/users/{userId}", userId)
                    .file(userUpdateRequestFile)
                    .file(profilePart)
                    .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.username").value("수정테스트"))
                .andExpect(jsonPath("$.email").value("newemail@example.com"));

        }

        @Test
        @DisplayName("사용자 수정 실패 - 중복된 이메일")
        void updateUser_File_DuplicateEmail() throws Exception {
            // Given
            UUID userId = UUID.randomUUID();
            UserUpdateRequest userUpdateRequest
                = new UserUpdateRequest("수정테스트", "newemail@example.com", "!password123!");

            given(userService.update(eq(userId), eq(userUpdateRequest), any()))
                .willThrow(new EmailAlreadyExistsException("newemail@example.com"));

            MockMultipartFile userUpdateRequestFile
                = toJsonPart("userUpdateRequest", userUpdateRequest);

            // When & Then
            mockMvc.perform(multipart(HttpMethod.PATCH, "/api/users/{userId}", userId)
                    .file(userUpdateRequestFile)
                    .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("이미 등록된 Email입니다."))
                .andExpect(jsonPath("$.details.email").value("newemail@example.com"));

        }

        @Test
        @DisplayName("사용자 수정 실패 - 중복된 사용자명")
        void updateUser_File_DuplicateUsername() throws Exception {
            // Given
            UUID userId = UUID.randomUUID();
            UserUpdateRequest userUpdateRequest
                = new UserUpdateRequest("수정테스트", "newemail@example.com", "!password123!");

            given(userService.update(eq(userId), eq(userUpdateRequest), any()))
                .willThrow(new UserNameAlreadyExistsException("수정테스트"));

            MockMultipartFile userUpdateRequestFile
                = toJsonPart("userUpdateRequest", userUpdateRequest);

            // When & Then
            mockMvc.perform(multipart(HttpMethod.PATCH, "/api/users/{userId}", userId)
                    .file(userUpdateRequestFile)
                    .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("이미 사용 중인 UserName입니다."))
                .andExpect(jsonPath("$.details.username").value("수정테스트"));

        }


    }

    @Nested
    @DisplayName("사용자 삭제 테스트")
    class DeleteUserTests {

        @Test
        @DisplayName("사용자 삭제 성공")
        void deleteUser_Success() throws Exception {
            //Given
            UUID userId = UUID.randomUUID();

            willDoNothing().given(userService).delete(userId);

            // When & Then
            mockMvc.perform(delete("/api/users/{userid}", userId))
                .andExpect(status().isNoContent());

        }

        @Test
        @DisplayName("유저 삭제 실패 - 존재하지 않는 ID")
        void deleteUser_Fail_NotFound() throws Exception {
            // Given
            UUID userId = UUID.randomUUID();

            willThrow(new UserNotFoundException(userId))
                .given(userService).delete(userId);

            // When & Then
            mockMvc.perform(delete("/api/users/{userId}", userId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User를 찾을 수 없습니다."));

        }
    }

    private MockMultipartFile toJsonPart(String partName, Object value) throws Exception {
        return new MockMultipartFile(
            partName,
            partName + ".json",
            "application/json",
            objectMapper.writeValueAsBytes(value)
        );
    }

    private MockMultipartFile fakeImagePart() {
        return new MockMultipartFile(
            "profile",
            "image.png",
            MediaType.IMAGE_PNG_VALUE,
            "fake-image-content".getBytes()
        );
    }

}
