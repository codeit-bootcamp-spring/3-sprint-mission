package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.exception.user.DuplicateUserException;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
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
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(UserController.class)
@ActiveProfiles("test")
@DisplayName("UserController 슬라이스 테스트")
@Import({GlobalExceptionHandler.class})
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
    @DisplayName("사용자 생성 - 성공(프로필 포함)")
    void create_Success() throws Exception {
        // Given
        UserCreateRequest request = new UserCreateRequest("조현아", "zzo@email.com",
            "password123!");
        BinaryContentDto profileImage = new BinaryContentDto(UUID.randomUUID(), "testImage", 1L,
            "png");
        UserDto userDto = new UserDto(UUID.randomUUID(), "조현아", "zzo@email.com", profileImage,
            null);
        String requestJson = objectMapper.writeValueAsString(request);
        MockMultipartFile userPart = new MockMultipartFile(
            "userCreateRequest",
            "",
            MediaType.APPLICATION_JSON_VALUE,
            requestJson.getBytes(StandardCharsets.UTF_8)
        );
        MockMultipartFile profile = new MockMultipartFile(
            "profile",
            "test.png",
            MediaType.IMAGE_PNG_VALUE,
            "fake-image-data".getBytes()
        );
        given(userService.create(any(), any())).willReturn(userDto);

        // When
        ResultActions result = mockMvc.perform(multipart("/api/users")
            .file(userPart)
            .file(profile)
            .contentType(MediaType.MULTIPART_FORM_DATA));

        // Then
        result.andExpect(status().isCreated())
            .andExpect(jsonPath("$.username").value("조현아"))
            .andExpect(jsonPath("$.email").value("zzo@email.com"))
            .andExpect(jsonPath("$.profile.fileName").value("testImage"))
            .andExpect(jsonPath("$.profile.contentType").value("png"))
            .andDo(print());
    }

    @Test
    @DisplayName("사용자 생성 - 유효성 검사 실패 (특수문자 포함 이름)")
    void create_Fail() throws Exception {
        // Given
        UserCreateRequest request = new UserCreateRequest("조@아", "zzo@email.com", "password123!");
        String requestJson = objectMapper.writeValueAsString(request);
        MockMultipartFile userPart = new MockMultipartFile(
            "userCreateRequest",
            "",
            MediaType.APPLICATION_JSON_VALUE,
            requestJson.getBytes(StandardCharsets.UTF_8)
        );

        // When
        ResultActions result = mockMvc.perform(
            multipart("/api/users")
                .file(userPart)
                .contentType(MediaType.MULTIPART_FORM_DATA));

        // Then
        result.andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
            .andDo(print());
    }

    @Test
    @DisplayName("유저 전체 조회 - 성공")
    void findAll_Success() throws Exception {
        // When
        ResultActions result = mockMvc.perform(get("/api/users"));

        // Then
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("유저 정보 수정 - 성공")
    void update_Success() throws Exception {
        // Given
        UUID userId = UUID.randomUUID();
        UserUpdateRequest request = new UserUpdateRequest("뉴현아", "new@email.com", "password123!");
        UserDto userDto = new UserDto(userId, request.newUsername(), request.newEmail(), null,
            null);
        String requestJson = objectMapper.writeValueAsString(request);
        MockMultipartFile userPart = new MockMultipartFile(
            "userUpdateRequest",
            "",
            MediaType.APPLICATION_JSON_VALUE,
            requestJson.getBytes(StandardCharsets.UTF_8)
        );
        given(userService.update(any(), any(), any())).willReturn(userDto);

        // When
        ResultActions result = mockMvc.perform(
            multipart(HttpMethod.PATCH, "/api/users/{userId}", userId)
                .file(userPart)
                .contentType(MediaType.MULTIPART_FORM_DATA));

        // Then
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("뉴현아"))
            .andExpect(jsonPath("$.email").value("new@email.com"))
            .andDo(print());
    }

    @Test
    @DisplayName("유저 정보 수정 - 실패")
    void update_Fail() throws Exception {
        // Given
        UUID userId = UUID.randomUUID();
        UserUpdateRequest request = new UserUpdateRequest("조현아", "new@email.com", "password123!");
        String requestJson = objectMapper.writeValueAsString(request);
        MockMultipartFile userPart = new MockMultipartFile(
            "userUpdateRequest",
            "",
            MediaType.APPLICATION_JSON_VALUE,
            requestJson.getBytes(StandardCharsets.UTF_8)
        );
        given(userService.update(any(), any(), any())).willThrow(new DuplicateUserException("조현아"));

        // When
        ResultActions result = mockMvc.perform(
            multipart(HttpMethod.PATCH, "/api/users/{userId}", userId)
                .file(userPart)
                .contentType(MediaType.MULTIPART_FORM_DATA));

        // Then
        result.andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value("DUPLICATE_USER"))
            .andDo(print());
    }

    @Test
    void delete_Success() throws Exception {
        // Given
        UUID userId = UUID.randomUUID();

        // When
        ResultActions result = mockMvc.perform(
            multipart(HttpMethod.DELETE, "/api/users/{userId}", userId));

        // Then
        result.andExpect(status().isNoContent())
            .andDo(print());
    }

}