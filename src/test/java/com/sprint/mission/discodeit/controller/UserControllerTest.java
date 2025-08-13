package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.user.UserUpdateRequest;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * UserController에 대한 슬라이스 테스트 클래스입니다.
 * <p>
 * 컨트롤러 레이어만을 독립적으로 테스트하며, 서비스 레이어는 Mock으로 대체하여 HTTP 요청/응답 처리 로직을 검증합니다.
 * <ul>
 *   <li>사용자 생성, 수정, 삭제, 전체 조회 등 다양한 시나리오를 검증합니다.</li>
 * </ul>
 */
@WebMvcTest(UserController.class)
@Import(GlobalExceptionHandler.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserStatusService userStatusService;

    private UUID testUserId;
    private UserDto testUserDto;

    /**
     * 각 테스트 메소드 실행 전에 공통으로 사용할 테스트 데이터를 초기화합니다.
     */
    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testUserDto = new UserDto(
            testUserId,
            "testuser",
            "test@example.com",
            null,
            true
        );
    }

    @Nested
    @DisplayName("사용자 생성 테스트")
    class CreateUserTest {

        /**
         * [성공] 유효한 사용자 정보로 사용자를 생성할 수 있는지 검증합니다.
         */
        @Test
        @DisplayName("[성공] 유효한 사용자 정보로 사용자 생성")
        void shouldCreateUserSuccessfully_whenValidUserDataProvided() throws Exception {
            // given: 유효한 사용자 생성 요청 데이터 준비
            UserCreateRequest createRequest = new UserCreateRequest(
                "testuser",
                "test@example.com",
                "password123"
            );
            
            given(userService.create(any(UserCreateRequest.class), any()))
                .willReturn(testUserDto);

            // when & then: POST 요청을 보내고 응답 검증
            mockMvc.perform(multipart("/api/users")
                    .file(new MockMultipartFile(
                        "userCreateRequest",
                        null,
                        MediaType.APPLICATION_JSON_VALUE,
                        objectMapper.writeValueAsBytes(createRequest)
                    ))
                    .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(testUserId.toString()))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
        }

        /**
         * [성공] 프로필 이미지와 함께 사용자를 생성할 수 있는지 검증합니다.
         */
        @Test
        @DisplayName("[성공] 프로필 이미지와 함께 사용자 생성")
        void shouldCreateUserWithProfileImage_whenProfileImageProvided() throws Exception {
            // given: 프로필 이미지가 포함된 사용자 생성 요청 데이터 준비
            UserCreateRequest createRequest = new UserCreateRequest(
                "testuser",
                "test@example.com",
                "password123"
            );
            
            MockMultipartFile profileImage = new MockMultipartFile(
                "profile",
                "profile.jpg",
                "image/jpeg",
                "test image content".getBytes()
            );
            
            given(userService.create(any(UserCreateRequest.class), any()))
                .willReturn(testUserDto);

            // when & then: 프로필 이미지와 함께 POST 요청을 보내고 응답 검증
            mockMvc.perform(multipart("/api/users")
                    .file(new MockMultipartFile(
                        "userCreateRequest",
                        null,
                        MediaType.APPLICATION_JSON_VALUE,
                        objectMapper.writeValueAsBytes(createRequest)
                    ))
                    .file(profileImage)
                    .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(testUserId.toString()));
        }

        /**
         * [실패] 잘못된 사용자 정보로 사용자 생성 시 400 Bad Request가 반환되는지 검증합니다.
         */
        @Test
        @DisplayName("[실패] 잘못된 사용자 정보로 사용자 생성 시 400 반환")
        void shouldReturnBadRequest_whenInvalidUserDataProvided() throws Exception {
            // given: 유효하지 않은 사용자 생성 요청 데이터 준비 (이메일 형식 오류)
            UserCreateRequest invalidRequest = new UserCreateRequest(
                "testuser",
                "invalid-email",
                "password123"
            );

            // when & then: 유효하지 않은 데이터로 POST 요청을 보내고 400 응답 검증
            mockMvc.perform(multipart("/api/users")
                    .file(new MockMultipartFile(
                        "userCreateRequest",
                        null,
                        MediaType.APPLICATION_JSON_VALUE,
                        objectMapper.writeValueAsBytes(invalidRequest)
                    ))
                    .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print())
                .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("사용자 수정 테스트")
    class UpdateUserTest {

        @Test
        @DisplayName("유효한 사용자 정보로 사용자를 수정하면 200 OK와 함께 수정된 사용자 정보를 반환한다")
        void shouldUpdateUserSuccessfully_whenValidUserDataProvided() throws Exception {
            // given: 유효한 사용자 수정 요청 데이터 준비
            UserUpdateRequest updateRequest = new UserUpdateRequest(
                "updateduser",
                "updated@example.com",
                null
            );
            
            UserDto updatedUserDto = new UserDto(
                testUserId,
                "updateduser",
                "updated@example.com",
                null,
                true
            );
            
            given(userService.update(eq(testUserId), any(UserUpdateRequest.class), any()))
                .willReturn(updatedUserDto);

            // when & then: PATCH 요청을 보내고 응답 검증
            mockMvc.perform(multipart("/api/users/" + testUserId)
                    .file(new MockMultipartFile(
                        "userUpdateRequest",
                        null,
                        MediaType.APPLICATION_JSON_VALUE,
                        objectMapper.writeValueAsBytes(updateRequest)
                    ))
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .with(request -> {
                        request.setMethod("PATCH");
                        return request;
                    }))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testUserId.toString()))
                .andExpect(jsonPath("$.username").value("updateduser"))
                .andExpect(jsonPath("$.email").value("updated@example.com"));
        }

        @Test
        @DisplayName("존재하지 않는 사용자 ID로 수정을 요청하면 USER_NOT_FOUND 상태코드로 응답한다")
        void shouldReturnNotFound_whenUserNotFound() throws Exception {
            // given: 존재하지 않는 사용자 ID와 유효한 수정 요청 데이터 준비
            UUID nonExistentUserId = UUID.randomUUID();
            UserUpdateRequest updateRequest = new UserUpdateRequest(
                "updateduser",
                "updated@example.com",
                null
            );
            
            given(userService.update(eq(nonExistentUserId), any(UserUpdateRequest.class), any()))
                .willThrow(new com.sprint.mission.discodeit.exception.user.UserNotFoundException("사용자를 찾을 수 없습니다."));

            // when & then: 존재하지 않는 사용자 ID로 PATCH 요청을 보내고 예외 응답 검증
            mockMvc.perform(multipart("/api/users/" + nonExistentUserId)
                    .file(new MockMultipartFile(
                        "userUpdateRequest",
                        null,
                        MediaType.APPLICATION_JSON_VALUE,
                        objectMapper.writeValueAsBytes(updateRequest)
                    ))
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .with(request -> {
                        request.setMethod("PATCH");
                        return request;
                    }))
                .andDo(print())
                .andExpect(status().is(com.sprint.mission.discodeit.exception.ErrorCode.USER_NOT_FOUND.getStatus().value()))
                .andExpect(jsonPath("$.code").value(com.sprint.mission.discodeit.exception.ErrorCode.USER_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.message").value(com.sprint.mission.discodeit.exception.ErrorCode.USER_NOT_FOUND.getMsg()));
        }
    }

    @Nested
    @DisplayName("사용자 삭제 테스트")
    class DeleteUserTest {

        @Test
        @DisplayName("존재하는 사용자 ID로 삭제를 요청하면 204 No Content를 반환한다")
        void shouldDeleteUserSuccessfully_whenValidUserIdProvided() throws Exception {
            // given: 사용자 삭제 서비스가 정상적으로 동작하도록 설정
            willDoNothing().given(userService).delete(testUserId);

            // when & then: DELETE 요청을 보내고 204 응답 검증
            mockMvc.perform(delete("/api/users/" + testUserId))
                .andDo(print())
                .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("존재하지 않는 사용자 ID로 삭제를 요청하면 USER_NOT_FOUND 상태코드로 응답한다")
        void shouldThrowException_whenUserNotFoundForDeletion() throws Exception {
            // given: 존재하지 않는 사용자 ID 준비
            UUID nonExistentUserId = UUID.randomUUID();
            willThrow(new com.sprint.mission.discodeit.exception.user.UserNotFoundException("사용자를 찾을 수 없습니다.")).given(userService).delete(nonExistentUserId);

            // when & then: 존재하지 않는 사용자 ID로 DELETE 요청을 보내고 예외 응답 검증
            mockMvc.perform(delete("/api/users/" + nonExistentUserId))
                .andDo(print())
                .andExpect(status().is(com.sprint.mission.discodeit.exception.ErrorCode.USER_NOT_FOUND.getStatus().value()))
                .andExpect(jsonPath("$.code").value(com.sprint.mission.discodeit.exception.ErrorCode.USER_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.message").value(com.sprint.mission.discodeit.exception.ErrorCode.USER_NOT_FOUND.getMsg()));
        }
    }

    @Nested
    @DisplayName("사용자 목록 조회 테스트")
    class FindAllUsersTest {

        @Test
        @DisplayName("전체 사용자 목록 조회 요청 시 200 OK와 함께 사용자 목록을 반환한다")
        void shouldReturnAllUsers_whenFindAllRequested() throws Exception {
            // given: 사용자 목록 데이터 준비
            UserDto user2 = new UserDto(
                UUID.randomUUID(),
                "user2",
                "user2@example.com",
                null,
                false
            );
            
            given(userService.findAll()).willReturn(Arrays.asList(testUserDto, user2));

            // when & then: GET 요청을 보내고 응답 검증
            mockMvc.perform(get("/api/users"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].username").value("testuser"))
                .andExpect(jsonPath("$[1].username").value("user2"));
        }

        @Test
        @DisplayName("사용자가 없을 때 조회 요청 시 200 OK와 함께 빈 목록을 반환한다")
        void shouldReturnEmptyList_whenNoUsersExist() throws Exception {
            // given: 빈 사용자 목록 반환하도록 설정
            given(userService.findAll()).willReturn(Arrays.asList());

            // when & then: GET 요청을 보내고 빈 목록 응답 검증
            mockMvc.perform(get("/api/users"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
        }
    }
}
