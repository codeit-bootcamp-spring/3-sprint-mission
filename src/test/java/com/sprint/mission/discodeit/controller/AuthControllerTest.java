package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.config.JpaConfig;
import com.sprint.mission.discodeit.dto.auth.LoginDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class,
        excludeAutoConfiguration = {JpaConfig.class})
@DisplayName("UserController 슬라이스 테스트")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @Test
    @DisplayName("등록된 유저로 로그인 시도를 하면 정상적으로 처리되어야 한다.")
    void givenValidLoginDto_whenLogin_thenReturnLoggedInUserResponse() throws Exception {

        // given
        LoginDto request = new LoginDto("test", "pwd12345");

        UUID userId = UUID.randomUUID();
        UserResponseDto loggedInUser = new UserResponseDto(
                userId,
                "test",
                "test@test.com",
                null,
                true
        );

        given(authService.login(any(LoginDto.class))).willReturn(loggedInUser);

        // when
        ResultActions result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.username").value("test"))
                .andExpect(jsonPath("$.email").value("test@test.com"))
                .andExpect(jsonPath("$.online").value(true));
    }
}