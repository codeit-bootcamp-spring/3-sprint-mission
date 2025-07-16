package com.sprint.mission.discodeit.intergration;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
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

    @Autowired
    private UserStatusRepository userStatusRepository;

    @Test
    @Transactional
    @DisplayName("유저 생성 - case : success")
    void createUserSuccess() throws Exception {
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

        mockMvc.perform(multipart("/api/users")
            .file(jsonPart)
            .file(profile)
            .with(req -> {
                req.setMethod("POST");
                return req;
            })
            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.username").value("김현기"))
            .andExpect(jsonPath("$.email").value("test@test.com"));

        LoginRequest loginRequest = new LoginRequest("김현기","009874");

        mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isOk());
    }

    @Test
    @Transactional
    @DisplayName("유저 생성 - case : 잘못된 이메일로 인한 failed")
    void createUserFail() throws Exception {
        UserCreateRequest request = new UserCreateRequest("김현기","test!test.com","009874");

        MockMultipartFile jsonPart = new MockMultipartFile(
            "userCreateRequest",
            "",
            MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsBytes(request)
        );

        mockMvc.perform(multipart("/api/users")
            .file(jsonPart)
            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
            .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    @DisplayName("유저 수정 - case : success")
    void updatedUserSuccess() throws Exception {
        User user = userRepository.save(new User("김현기","test@test.com","009874",null));
        UserStatus userStatus = userStatusRepository.save(new UserStatus(user, Instant.now()));
        UUID userId = user.getId();

        UserUpdateRequest updateRequest = new UserUpdateRequest("테스트맨","test2@test.com",null);

        MockMultipartFile jsonPart = new MockMultipartFile(
            "userUpdateRequest",
            "",
            "application/json",
            objectMapper.writeValueAsBytes(updateRequest)
        );

        mockMvc.perform(multipart("/api/users/{userId}",userId)
            .file(jsonPart)
            .with(req -> {
                req.setMethod("PATCH");
                return req;
            })
            .contentType(MediaType.MULTIPART_FORM_DATA)
        )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("테스트맨"))
            .andExpect(jsonPath("$.email").value("test2@test.com"));
    }

    @Test
    @Transactional
    @DisplayName("유저 목록 조회 - case : success")
    void readUserSuccess() throws Exception{
        User user1 = new User("김현기","test@test.com","009874",null);
        User user2 = new User("테스트맨","test2@test.com","555555",null);

        userRepository.save(user1);
        userRepository.save(user2);

        UserStatus userStatus1 = userStatusRepository.save(new UserStatus(user1, Instant.now()));
        UserStatus userStatus2 = userStatusRepository.save(new UserStatus(user2, Instant.now()));

        mockMvc.perform(get("/api/users")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].username").value("김현기"))
            .andExpect(jsonPath("$[1].username").value("테스트맨"));
    }

    @Test
    @Transactional
    void deleteUserSuccess() throws Exception{
        UserCreateRequest request = new UserCreateRequest("testMan","test3@test.com","12345");
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

        String response = mockMvc.perform(multipart("/api/users")
            .file(jsonPart)
            .file(profile)
            .with(req -> {
                req.setMethod("POST");
                return req;
            })
            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();

        UserDto userDto = objectMapper.readValue(response,UserDto.class);
        UUID userId = userDto.id();

        mockMvc.perform(delete("/api/users/" + userId))
            .andExpect(status().isNoContent());

        LoginRequest loginRequest = new LoginRequest("testMan","12345");
        mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("유저를 찾을 수 없습니다."));
    }
}
