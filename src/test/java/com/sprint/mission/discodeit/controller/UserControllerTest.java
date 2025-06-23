package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.config.JpaConfig;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponseDto;
import com.sprint.mission.discodeit.dto.user.UserRequestDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import com.sprint.mission.discodeit.service.basic.BasicUserStatusService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class,
        excludeAutoConfiguration = {JpaConfig.class})
@DisplayName("UserController 슬라이스 테스트")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BasicUserService userService;

    @MockitoBean
    private BasicUserStatusService userStatusService;

    @Test
    @DisplayName("사용자 생성 요청이 올바르게 처리되어야 한다.")
    void givenValidUserRequest_whenCreateUser_thenReturnCreatedUserResponse()
            throws Exception {

        // given
        UUID userId = UUID.randomUUID();
        UserRequestDto request = new UserRequestDto("test", "test@test.com", "pwd1234");
        BinaryContentResponseDto profileImage = new BinaryContentResponseDto(UUID.randomUUID(), "profile.png", 3L,
                "image/png");

        UserResponseDto expectedResponse = new UserResponseDto(userId, "test", "test@test.com", profileImage, null);

        MockMultipartFile userCreateRequest = new MockMultipartFile(
                "userCreateRequest",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(request)
        );

        MockMultipartFile profile = new MockMultipartFile(
                "profile",
                "profile.png",
                "image/png",
                new byte[]{1, 2, 3}
        );

        given(userService.create(any(UserRequestDto.class), any(BinaryContentDto.class)))
                .willReturn(expectedResponse);

        // when
        mockMvc.perform(multipart("/api/users")
                        .file(userCreateRequest)
                        .file(profile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.username").value("test"))
                .andExpect(jsonPath("$.email").value("test@test.com"));

        // then
        verify(userService).create(argThat(req ->
                req.username().equals("test") &&
                        req.email().equals("test@test.com")
        ), any());
    }

    @Test
    @DisplayName("필수 항목이 누락되면 400 Bad Request가 발생해야 한다.")
    void givenMissingRequiredField_whenCreateUser_thenReturnBadRequestWithValidationError()
            throws Exception {

        // given
        UserRequestDto request = new UserRequestDto(null, "test@test.com", "pwd1234");

        MockMultipartFile userCreateRequest = new MockMultipartFile(
                "userCreateRequest",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(request)
        );

        // when
        mockMvc.perform(multipart("/api/users")
                        .file(userCreateRequest)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.message").value("유효성 검사 실패"));
    }
}