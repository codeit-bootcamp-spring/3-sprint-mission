package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(controllers = UserController.class)
@DisplayName("UserController 슬라이스 테스트")
public class UserControllerTest {

    @Autowired private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private UserService userService;
    @MockitoBean private UserStatusService userStatusService;

    @Test
    @DisplayName("유저 생성 API가 정상적으로 동작한다")
    void createUser_Success() throws Exception {
        // given
        UserCreateRequest userCreateRequest = new UserCreateRequest("테스트", "test@gmail.com", "000000000");
        UserDto userDto = new UserDto(UUID.randomUUID(), "테스트", "test@gmail.com", null, null);

        given(userService.create(userCreateRequest, Optional.empty())).willReturn(userDto);

        MockMultipartFile userCreateRequestFile = new MockMultipartFile(
                "userCreateRequest",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(userCreateRequest)
        );

        // when & then
        mockMvc.perform(multipart("/api/users")
                        .file(userCreateRequestFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("테스트"))
                .andExpect(jsonPath("$.email").value("test@gmail.com"));
    }

    @Test
    @DisplayName("비밀번호 8자 미만일 시 생성 실패")
    void createUser_Fail() throws Exception {
        // given
        UserCreateRequest userCreateRequest = new UserCreateRequest("테스트", "test@gmail.com", "0000000");
        UserDto userDto = new UserDto(UUID.randomUUID(), "테스트", "test@gmail.com", null, null);

        given(userService.create(userCreateRequest, Optional.empty())).willReturn(userDto);

        MockMultipartFile userCreateRequestFile = new MockMultipartFile(
                "userCreateRequest",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(userCreateRequest)
        );

        // when & then
        mockMvc.perform(multipart("/api/users")
                        .file(userCreateRequestFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                .andExpect(jsonPath("$.details.message").value(org.hamcrest.Matchers.containsString("password")));
    }
}
