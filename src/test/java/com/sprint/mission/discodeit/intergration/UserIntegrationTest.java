package com.sprint.mission.discodeit.intergration;

import static org.junit.Assert.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@AutoConfigureMockMvc
@DisplayName("유저 통합 테스트")
public class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @Transactional
    @DisplayName("유저 생성 - case : success")
    void createUserSuccess() throws Exception {
        // Given
        UserCreateRequest request = new UserCreateRequest("김현기","test@test.com","009874");
        MockMultipartFile jsonPart = new MockMultipartFile(
            "userCreateRequest",
            "",
            "application/json",
            objectMapper.writeValueAsBytes(request)
        );
        MockMultipartFile profile = new MockMultipartFile(
            "profile",
            "profile.jpg",
            "image/jpeg",
            "profile image".getBytes()
        );

        // When - 유저 생성 요청
        ResultActions createResult = mockMvc.perform(multipart("/api/users")
            .file(jsonPart)
            .file(profile)
            .with(req -> {
                req.setMethod("POST");
                return req;
            })
            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
        );

        // Then - 생성 결과 검증
        createResult.andExpect(status().isCreated())
            .andExpect(jsonPath("$.username").value("김현기"))
            .andExpect(jsonPath("$.email").value("test@test.com"));
    }

    @Test
    @Transactional
    @DisplayName("유저 생성 - case : 잘못된 이메일로 인한 failed")
    void createUserFail() throws Exception {
        // Given
        UserCreateRequest request = new UserCreateRequest("김현기","test!test.com","009874");
        MockMultipartFile jsonPart = new MockMultipartFile(
            "userCreateRequest",
            "",
            MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsBytes(request)
        );

        // When
        ResultActions result = mockMvc.perform(multipart("/api/users")
            .file(jsonPart)
            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
            .with(req -> {
                req.setMethod("POST");
                return req;
            })
        );

        // Then
        result.andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    @DisplayName("유저 수정 - case : 본인 수정 성공")
    void updateUserSuccess() throws Exception {
        // Given - 사용자 생성
        User user = userRepository.save(new User("김현기","test@test.com","009874",null));
        UUID userId = user.getId();

        // 해당 사용자로 인증된 상태로 만들기
        UserDto userDto = new UserDto(userId, "김현기", "test@test.com", Role.USER, null,true);
        DiscodeitUserDetails userDetails = new DiscodeitUserDetails(userDto, "009874");

        UserUpdateRequest updateRequest = new UserUpdateRequest("테스트맨","test2@test.com",null);
        MockMultipartFile jsonPart = new MockMultipartFile(
            "userUpdateRequest",
            "",
            "application/json",
            objectMapper.writeValueAsBytes(updateRequest)
        );

        // When - 본인 계정 수정
        ResultActions result = mockMvc.perform(multipart("/api/users/{userId}", userId)
            .file(jsonPart)
            .with(user(userDetails)) // 본인으로 인증
            .with(req -> {
                req.setMethod("PATCH");
                return req;
            })
            .contentType(MediaType.MULTIPART_FORM_DATA)
        );

        // Then
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("테스트맨"))
            .andExpect(jsonPath("$.email").value("test2@test.com"));
    }

    @Test
    @Transactional
    @DisplayName("유저 수정 - case : 다른 사용자 수정 시도 시 권한 없음")
    void updateUserForbidden() throws Exception {
        // Given - 두 명의 사용자 생성
        User user1 = userRepository.save(new User("김현기","test@test.com","009874",null));
        User user2 = userRepository.save(new User("테스트맨","test2@test.com","555555",null));

        // user1로 인증하지만 user2의 정보를 수정 시도
        UserDto userDto = new UserDto(user1.getId(), "김현기", "test@test.com", Role.USER,null,true);
        DiscodeitUserDetails userDetails = new DiscodeitUserDetails(userDto, "009874");

        UserUpdateRequest updateRequest = new UserUpdateRequest("해커맨","hacker@test.com",null);
        MockMultipartFile jsonPart = new MockMultipartFile(
            "userUpdateRequest",
            "",
            "application/json",
            objectMapper.writeValueAsBytes(updateRequest)
        );

        // When - 다른 사용자 계정 수정 시도
        ResultActions result = mockMvc.perform(multipart("/api/users/{userId}", user2.getId())
            .file(jsonPart)
            .with(user(userDetails)) // user1로 인증했지만 user2 수정 시도
            .with(req -> {
                req.setMethod("PATCH");
                return req;
            })
            .contentType(MediaType.MULTIPART_FORM_DATA)
        );

        // Then - 권한 없음 오류
        result.andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    @DisplayName("유저 목록 조회 - case : success")
    void readUserSuccess() throws Exception {
        // Given
        User user1 = new User("김현기","test@test.com","009874",null);
        User user2 = new User("테스트맨","test2@test.com","555555",null);
        userRepository.save(user1);
        userRepository.save(user2);

        // When
        ResultActions result = mockMvc.perform(get("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
        );

        // Then
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].username").value("김현기"))
            .andExpect(jsonPath("$[1].username").value("테스트맨"));
    }

    @Test
    @Transactional
    @DisplayName("유저 삭제 - case : 본인 삭제 성공")
    void deleteUserSuccess() throws Exception {
        // Given - 사용자 생성
        User user = userRepository.save(new User("testMan","test3@test.com","12345",null));
        UUID userId = user.getId();

        // 해당 사용자로 인증된 상태로 만들기
        UserDto userDto = new UserDto(userId, "testMan", "test3@test.com", Role.USER,null,true);
        DiscodeitUserDetails userDetails = new DiscodeitUserDetails(userDto, "12345");

        // When - 본인 계정 삭제
        mockMvc.perform(delete("/api/users/" + userId)
                .with(user(userDetails))) // 본인으로 인증
            .andExpect(status().isNoContent());

        // Then - 삭제 확인 (사용자가 존재하지 않아야 함)
        // 실제로는 사용자가 삭제되어서 로그인이 안 되는지 확인하는 것보다는
        // 레포지토리에서 직접 확인하는 것이 더 정확함
        assertTrue(userRepository.findById(userId).isEmpty());
    }

    @Test
    @Transactional
    @DisplayName("유저 삭제 - case : 다른 사용자 삭제 시도 시 권한 없음")
    void deleteUserForbidden() throws Exception {
        // Given - 두 명의 사용자 생성
        User user1 = userRepository.save(new User("김현기","test@test.com","009874",null));
        User user2 = userRepository.save(new User("테스트맨","test2@test.com","555555",null));

        // user1로 인증하지만 user2를 삭제 시도
        UserDto userDto = new UserDto(user1.getId(), "김현기", "test@test.com", Role.USER,null,true);
        DiscodeitUserDetails userDetails = new DiscodeitUserDetails(userDto, "009874");

        // When - 다른 사용자 삭제 시도
        ResultActions result = mockMvc.perform(delete("/api/users/" + user2.getId())
            .with(user(userDetails))); // user1로 인증했지만 user2 삭제 시도

        // Then - 권한 없음 오류
        result.andExpect(status().isForbidden());
    }
}