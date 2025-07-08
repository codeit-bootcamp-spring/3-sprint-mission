package com.sprint.mission.discodeit.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
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
    private UserStatusRepository userStatusRepository;

    private User savedUser1;
    private User savedUser2;
    private BinaryContent savedBinaryContent;

    @BeforeEach
    void setUp() {
        // BinaryContent 생성
        BinaryContent binaryContent = new BinaryContent("profile1.png", 102400L, "image/png");
        savedBinaryContent = binaryContentRepository.save(binaryContent);

        // User 생성
        User user1 = new User("조현아", "zzo@email.com", "password123!", savedBinaryContent);
        savedUser1 = userRepository.save(user1);

        User user2 = new User("투현아", "z2@email.com", "password123!", null);
        savedUser2 = userRepository.save(user2);

        // UserStatus 생성
        UserStatus userStatus1 = new UserStatus(savedUser1, Instant.now());
        userStatusRepository.save(userStatus1);

        UserStatus userStatus2 = new UserStatus(savedUser2, Instant.now());
        userStatusRepository.save(userStatus2);
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
        // When
        ResultActions result = mockMvc.perform(get("/api/users")
            .contentType(MediaType.APPLICATION_JSON));

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
        String userId = savedUser1.getId().toString();
        UserUpdateRequest request = new UserUpdateRequest("뉴현아", "updated@email.com",
            "newPassword123!");
        String json = objectMapper.writeValueAsString(request);
        MockMultipartFile userPart = new MockMultipartFile(
            "userUpdateRequest", "", "application/json", json.getBytes(StandardCharsets.UTF_8)
        );

        // When
        ResultActions result = mockMvc.perform(
            multipart(HttpMethod.PATCH, "/api/users/{id}", userId)
                .file(userPart)
                .contentType(MediaType.MULTIPART_FORM_DATA));

        // Then
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("뉴현아"))
            .andExpect(jsonPath("$.email").value("updated@email.com"))
            .andDo(print());
    }

    @Test
    @DisplayName("유저 수정 - 실패 (존재하지 않는 유저)")
    void updateUser_Failure_UserNotFound() throws Exception {
        // Given
        String nonExistentUserId = "99999999-9999-9999-9999-999999999999";
        UserUpdateRequest request = new UserUpdateRequest("뉴현아", "updated@email.com",
            "newPassword123!");
        String json = objectMapper.writeValueAsString(request);

        MockMultipartFile userPart = new MockMultipartFile(
            "userUpdateRequest", "", "application/json", json.getBytes(StandardCharsets.UTF_8)
        );

        // When
        ResultActions result = mockMvc.perform(
            multipart(HttpMethod.PATCH, "/api/users/{id}", nonExistentUserId)
                .file(userPart)
                .contentType(MediaType.MULTIPART_FORM_DATA));

        // Then
        result.andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").exists())
            .andDo(print());
    }

    @Test
    @DisplayName("유저 삭제 - 성공")
    void deleteUser_Success() throws Exception {
        // Given
        String userId = savedUser1.getId().toString();

        // When
        ResultActions result = mockMvc.perform(delete("/api/users/{id}", userId)
            .contentType(MediaType.APPLICATION_JSON));

        // Then
        result.andExpect(status().isNoContent())
            .andDo(print());
    }

    @Test
    @DisplayName("유저 삭제 - 실패 (존재하지 않는 유저)")
    void deleteUser_Fail_UserNotFound() throws Exception {
        // Given
        String nonExistentUserId = "99999999-9999-9999-9999-999999999999";

        // When
        ResultActions result = mockMvc.perform(delete("/api/users/{id}", nonExistentUserId)
            .contentType(MediaType.APPLICATION_JSON));

        // Then
        result.andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").exists())
            .andDo(print());
    }
}
