package com.sprint.mission.discodeit.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserApiIntegrationTest {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private UserRepository userRepository;

  @Test
  @DisplayName("유저 생성 -> 전체 조회")
  void createUser_thenFindAll() throws Exception {
// given: 유저 생성 요청  준비
    UserCreateRequest createRequest = new UserCreateRequest("integrationUser",
        "integration@test.com", "password123");
    MockMultipartFile userPart = new MockMultipartFile(
        "userCreateRequest",
        null,
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(createRequest));

    // when: 유저 생성 요청
    mockMvc.perform(multipart("/api/users")
            .file(userPart)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.username").value("integrationUser"))
        .andExpect(jsonPath("$.email").value("integration@test.com"));

    // then: 전체 유저 조회
    mockMvc.perform(get("/api/users"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].username").exists());

  }

  @Test
  @DisplayName("유저 생성(username 누락) 실패 -> 전체 조회")
  void createUser_withInvalidInput_thenFail() throws Exception {
    // given: username 누락된 요청
    UserCreateRequest invalidRequest = new UserCreateRequest("", "invalid@test.com", "pass");
    MockMultipartFile userPart = new MockMultipartFile(
        "userCreateRequest",
        null,
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(invalidRequest)
    );

    // when & then: 404
    mockMvc.perform(multipart("/api/users")
            .file(userPart)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isBadRequest());
  }

}
