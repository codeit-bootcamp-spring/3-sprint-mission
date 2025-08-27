package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.exception.user.DuplicateUserException;
import com.sprint.mission.discodeit.fixture.BinaryContentFixture;
import com.sprint.mission.discodeit.fixture.UserFixture;
import com.sprint.mission.discodeit.service.UserService;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
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

    @WithMockUser
    @Test
    @DisplayName("사용자 생성 - 성공(프로필 포함)")
    void create_Success() throws Exception {
        // Given
        BinaryContentDto profileDto = BinaryContentFixture.dto();
        UserDto userDto = UserFixture.dto("프로필유저", "profile@email.com", profileDto, Role.USER);
        UserCreateRequest request = UserFixture.createRequest("프로필유저", "profile@email.com");
        String requestJson = objectMapper.writeValueAsString(request);
        MockMultipartFile userPart = new MockMultipartFile(
            "userCreateRequest",
            "",
            MediaType.APPLICATION_JSON_VALUE,
            requestJson.getBytes(StandardCharsets.UTF_8)
        );
        MockMultipartFile profile = new MockMultipartFile(
            "profile",
            "testImage.png",
            MediaType.IMAGE_PNG_VALUE,
            "fake-image-data".getBytes()
        );
        given(userService.create(any(), any())).willReturn(userDto);

        // When
        ResultActions result = mockMvc.perform(multipart("/api/users")
            .file(userPart)
            .file(profile)
            .with(csrf())
            .contentType(MediaType.MULTIPART_FORM_DATA));

        // Then
        result.andExpect(status().isCreated())
            .andExpect(jsonPath("$.username").value("프로필유저"))
            .andExpect(jsonPath("$.email").value("profile@email.com"))
            .andExpect(jsonPath("$.profile.fileName").value("testImage"))
            .andExpect(jsonPath("$.profile.contentType").value("png"))
            .andDo(print());
    }

    @WithMockUser
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
                .with(csrf())
                .contentType(MediaType.MULTIPART_FORM_DATA));

        // Then
        result.andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
            .andDo(print());
    }

    @WithMockUser
    @Test
    @DisplayName("유저 전체 조회 - 성공")
    void findAll_Success() throws Exception {
        // Given
        given(userService.findAll()).willReturn(List.of());

        // When
        ResultActions result = mockMvc.perform(get("/api/users"));

        // Then
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(0));
    }

    @WithMockUser
    @Test
    @DisplayName("유저 정보 수정 - 성공")
    void update_Success() throws Exception {
        // Given
        UUID userId = UUID.randomUUID();
        UserUpdateRequest request = new UserUpdateRequest("뉴현아", "new@email.com", "password123!");
        UserDto userDto = new UserDto(userId, request.newUsername(), request.newEmail(), null,
            null, Role.USER);
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
                .with(csrf())
                .contentType(MediaType.MULTIPART_FORM_DATA));

        // Then
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("뉴현아"))
            .andExpect(jsonPath("$.email").value("new@email.com"))
            .andDo(print());
    }

    @WithMockUser
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
                .with(csrf())
                .contentType(MediaType.MULTIPART_FORM_DATA));

        // Then
        result.andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value("DUPLICATE_USER"))
            .andDo(print());
    }

    @WithMockUser
    @Test
    void delete_Success() throws Exception {
        // Given
        UUID userId = UUID.randomUUID();

        // When
        ResultActions result = mockMvc.perform(delete("/api/users/{userId}", userId)
            .with(csrf()));

        // Then
        result.andExpect(status().isNoContent())
            .andDo(print());
    }

}