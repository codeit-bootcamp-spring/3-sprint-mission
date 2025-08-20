package com.sprint.mission.discodeit.integration;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.DiscodeitUserDetails;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@DisplayName("User API 통합테스트")
public class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BinaryContentRepository binaryContentRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    private User savedUser1;
    private User savedUser2;
    private BinaryContent savedBinaryContent;


    @BeforeEach
    void setUp() {
        // BinaryContent 생성
        BinaryContent binaryContent = new BinaryContent("profile1.png", 102400L, "image/png");
        savedBinaryContent = binaryContentRepository.save(binaryContent);

        // User 생성
        User user1 = new User("조현아", "zzo@email.com", passwordEncoder.encode("password123!"), savedBinaryContent);
        savedUser1 = userRepository.save(user1);

        User user2 = new User("투현아", "z2@email.com", passwordEncoder.encode("password123!"), null);
        savedUser2 = userRepository.save(user2);
    }

    @Test
    @DisplayName("유저 생성 - 성공")
    void createUser_Success() throws Exception {
        // Given
        UserCreateRequest request = new UserCreateRequest("생성유저", "create@email.com",
            "password123!");
        String json = objectMapper.writeValueAsString(request);
        MockMultipartFile userPart = new MockMultipartFile(
            "userCreateRequest", "", "application/json", json.getBytes(StandardCharsets.UTF_8)
        );

        // When
        ResultActions result = mockMvc.perform(multipart("/api/users")
            .file(userPart)
            .with(csrf())
            .contentType(MediaType.MULTIPART_FORM_DATA));

        // Then
        result.andExpect(status().isCreated())
            .andExpect(jsonPath("$.username").value("생성유저"))
            .andExpect(jsonPath("$.email").value("create@email.com"))
            .andDo(print());
    }

    @Test
    @DisplayName("유저 전체 조회 - 성공")
    void getUserAll_Success() throws Exception {
        // Given
        MockHttpSession session = loginAndGetSession(savedUser1.getUsername(), "password123!");

        // When
        ResultActions result = mockMvc.perform(get("/api/users")
            .session(session));

        // Then
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))  // 조현아, 투현아 2명
            .andExpect(jsonPath("$[0].username").value("조현아"))
            .andExpect(jsonPath("$[0].email").value("zzo@email.com"))
            .andExpect(jsonPath("$[1].username").value("투현아"))
            .andExpect(jsonPath("$[1].email").value("z2@email.com"))
            .andDo(print());
    }

    @Test
    @DisplayName("유저 수정 - 성공")
    void updateUser_Success() throws Exception {
        // Given
        MockHttpSession session = loginAndGetSession(savedUser1.getUsername(), "password123!");
        String userId = savedUser1.getId().toString();
        UserUpdateRequest request = new UserUpdateRequest("뉴현아", "updated@email.com",
            "newPassword123!");
        String json = objectMapper.writeValueAsString(request);
        MockMultipartFile userPart = new MockMultipartFile(
            "userUpdateRequest", "", "application/json", json.getBytes(StandardCharsets.UTF_8)
        );

        // When
        ResultActions result = mockMvc.perform(
            multipart(HttpMethod.PATCH, "/api/users/{userId}", userId)
                .file(userPart)
                .with(csrf())
                .session(session));

        // Then
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("뉴현아"))
            .andExpect(jsonPath("$.email").value("updated@email.com"))
            .andDo(print());
    }

    @Test
    @DisplayName("유저 수정 - 실패 (권한 없음)")
    void updateUser_Failure_Forbidden() throws Exception {
        // Given
        String targetUserId = savedUser1.getId().toString();
        UserDto otherUserDto = userMapper.toDto(savedUser2);
        DiscodeitUserDetails principal = new DiscodeitUserDetails(otherUserDto, "{noop}pwd");
        UserUpdateRequest request = new UserUpdateRequest("뉴현아", "updated@email.com", "newPassword123!");
        String json = objectMapper.writeValueAsString(request);
        MockMultipartFile userPart = new MockMultipartFile(
            "userUpdateRequest", "", "application/json",
            json.getBytes(StandardCharsets.UTF_8)
        );

        // When
        ResultActions result = mockMvc.perform(
            multipart(HttpMethod.PATCH, "/api/users/{userId}", targetUserId)
                .file(userPart)
                .with(user(principal))
                .with(csrf())
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON)
        );

        // Then
        result.andExpect(status().isForbidden())
        .andDo(print());
    }

    @Test
    @DisplayName("유저 삭제 - 성공")
    void deleteUser_Success() throws Exception {
        // Given
        MockHttpSession session = loginAndGetSession(savedUser1.getUsername(), "password123!");
        String userId = savedUser1.getId().toString();

        // When
        ResultActions result = mockMvc.perform(delete("/api/users/{userId}", userId)
            .with(csrf())
            .session(session));

        // Then
        result.andExpect(status().isNoContent())
            .andDo(print());
    }

    @Test
    @DisplayName("유저 삭제 - 실패 (권한 없는 유저)")
    void deleteUser_Fail_UserNotFound() throws Exception {
        // Given
        MockHttpSession session = loginAndGetSession(savedUser1.getUsername(), "password123!");
        userRepository.deleteById(savedUser2.getId());

        // When
        ResultActions result = mockMvc.perform(delete("/api/users/{userId}", savedUser2.getId())
            .with(csrf())
            .session(session));

        // Then
        result.andExpect(status().isForbidden())
            .andExpect(jsonPath("$.message").exists())
            .andDo(print());
    }

    @Test
    void secured_call() throws Exception {
        MockHttpSession session = loginAndGetSession("조현아", "password123!");
        mockMvc.perform(get("/api/users").session(session))
            .andExpect(status().isOk());
    }

    // 로그인
    private MockHttpSession loginAndGetSession(String username, String password) throws Exception {
        var result = mockMvc.perform(post("/api/auth/login")
                .param("username", username)
                .param("password", password)
                .with(csrf()))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();
        return (MockHttpSession) result.getRequest().getSession(false);
    }

}
