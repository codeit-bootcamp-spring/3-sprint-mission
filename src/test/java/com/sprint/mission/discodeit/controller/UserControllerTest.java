package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.service.UserService;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@ActiveProfiles("test")
@WebMvcTest(
    controllers = UserController.class,
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = GlobalExceptionHandler.class
    )
)
@WithMockUser
@DisplayName("UserController 슬라이스 테스트")
public class UserControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private UserService userService;

    @Test
    @DisplayName("사용자 생성 API 성공")
    void createUser() throws Exception {
        // given
        UserCreateRequest userCreateRequest = new UserCreateRequest("tom", "tom@test.com", "pw123456");
        UserDto userDto = new UserDto(
            UUID.randomUUID(),
            "tom",
            "tom@test.com",
            null,
            false,
            Role.USER
        );
        given(userService.create(userCreateRequest,Optional.empty())).willReturn(userDto);

        // when, then
        MockMultipartFile userCreateRequestFile = new MockMultipartFile(
            "userCreateRequest",
            "",
            "application/json",
            objectMapper.writeValueAsBytes(userCreateRequest)
        );

        mockMvc.perform(multipart("/api/users")
                .file(userCreateRequestFile)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .with(csrf()))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.username").value("tom"))
            .andExpect(jsonPath("$.email").value("tom@test.com"))
            .andExpect(jsonPath("$.profile").doesNotExist());
    }

    @Test
    @DisplayName("사용자 업데이트 API 성공")
    void updateUser() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        UserUpdateRequest updateReq = new UserUpdateRequest("tommy", "tommy@test.com", "pw223456");

        UUID contentId = UUID.randomUUID();
        BinaryContentDto profileDto = new BinaryContentDto(contentId, "profile.png", 1024L, "image/png");

        UserDto returned = new UserDto(eq(userId), "tommy", "tommy@test.com", profileDto, false, Role.USER);
        given(userService.update(userId, any(UserUpdateRequest.class), any(Optional.class))).willReturn(returned);

        MockMultipartFile userInfoPart = new MockMultipartFile(
            "userUpdateRequest",
            "user.json",
            MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsBytes(updateReq)
        );
        MockMultipartFile filePart = new MockMultipartFile(
            "profile",
            "profile.png",
            MediaType.IMAGE_PNG_VALUE,
            "dummy-image-bytes".getBytes()
        );

        MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart(
            HttpMethod.PATCH, "/api/users/" + userId
        );
        builder.file(userInfoPart)
            .file(filePart)
            .with(csrf())
            .with(request -> { request.setMethod("PATCH"); return request; });

        // when, then
        mockMvc.perform(builder)
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.username").value("tommy"))
            .andExpect(jsonPath("$.email").value("tommy@test.com"));

        then(userService).should(times(1))
            .update(eq(userId), any(UserUpdateRequest.class), any(Optional.class));
    }

    @Test
    @DisplayName("사용자 삭제 API 성공")
    void deleteUser() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        willDoNothing().given(userService).delete(userId);

        // when, then
        mockMvc.perform(delete("/api/users/{userId}", userId)
                .with(csrf()))
            .andExpect(status().isNoContent());
        then(userService).should(times(1)).delete(userId);
    }
}