package com.sprint.mission.discodeit.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@Tag("integration")
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("security-test")
class AuthControllerTest {

  @Autowired
  MockMvc mockMvc;

  @Autowired
  UserRepository userRepository;

  @Autowired
  PasswordEncoder passwordEncoder;

  @BeforeEach
  void setUp() {
    userRepository.deleteAll();
  }

  @Test
  void 로그인_성공() throws Exception {
    User user = User.create("test@test.com", "tester", passwordEncoder.encode("password"), null);
    userRepository.save(user);

    String token = fetchCsrfToken();

    mockMvc.perform(post("/api/auth/login")
            .cookie(new Cookie("XSRF-TOKEN", token))
            .header("X-XSRF-TOKEN", token)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("username", "tester")
            .param("password", "password"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.user.id").value(user.getId().toString()))
        .andExpect(jsonPath("$.user.username").value("tester"))
        .andExpect(jsonPath("$.user.email").value("test@test.com"));
  }

  @Test
  void 로그인_실패() throws Exception {
    User user = User.create("test@test.com", "tester", passwordEncoder.encode("password"), null);
    userRepository.save(user);

    String token = fetchCsrfToken();

    mockMvc.perform(post("/api/auth/login")
            .cookie(new Cookie("XSRF-TOKEN", token))
            .header("X-XSRF-TOKEN", token)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("username", "tester")
            .param("password", "wrong"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.errorCode").value("UNAUTHORIZED"));
  }

  // TODO: JWT 인증 필터 구현 후 활성화
  @Test
  @Disabled("JWT 인증 필터 구현 후 활성화 필요")
  void 세션으로_현재_사용자_정보를_조회한다() throws Exception {
    User user = User.create("test@test.com", "tester", passwordEncoder.encode("password"), null);
    userRepository.save(user);

    String token = fetchCsrfToken();

    MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
            .cookie(new Cookie("XSRF-TOKEN", token))
            .header("X-XSRF-TOKEN", token)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("username", "tester")
            .param("password", "password"))
        .andExpect(status().isOk())
        .andReturn();

    String responseBody = loginResult.getResponse().getContentAsString();
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode = objectMapper.readTree(responseBody);
    String accessToken = jsonNode.get("accessToken").asText();

    mockMvc.perform(
            get("/api/auth/me")
                .header("Authorization", "Bearer " + accessToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(user.getId().toString()))
        .andExpect(jsonPath("$.username").value("tester"))
        .andExpect(jsonPath("$.email").value("test@test.com"));
  }

  @Test
  void 로그인_없이_me_조회하면_401() throws Exception {
    mockMvc.perform(get("/api/auth/me"))
        .andExpect(status().isUnauthorized());
  }

  // TODO: JWT 인증 필터 구현 후 활성화
  @Test
  @Disabled("JWT 인증 필터 구현 후 리팩터링 필요")
  void remember_me_쿠키로_로그인_유지() throws Exception {
    User user = User.create("test@test.com", "tester", passwordEncoder.encode("password"), null);
    userRepository.save(user);

    String token = fetchCsrfToken();

    MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
            .cookie(new Cookie("XSRF-TOKEN", token))
            .header("X-XSRF-TOKEN", token)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("username", "tester")
            .param("password", "password")
            .param("remember-me", "true"))
        .andExpect(status().isOk())
        .andReturn();

    Cookie rememberMe = Objects.requireNonNull(
        loginResult.getResponse().getCookie("remember-me"), "remember-me 쿠키가 없습니다");

    mockMvc.perform(get("/api/auth/me").cookie(rememberMe))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(user.getId().toString()))
        .andExpect(jsonPath("$.username").value("tester"))
        .andExpect(jsonPath("$.email").value("test@test.com"));
  }

  // TODO: JWT 인증 필터 구현 후 활성화
  @Test
  @Disabled("JWT 인증 필터 구현 후 리팩터링 필요")
  void 로그아웃_성공() throws Exception {
    User user = User.create("test@test.com", "tester", passwordEncoder.encode("password"), null);
    userRepository.save(user);

    String token = fetchCsrfToken();

    MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
            .cookie(new Cookie("XSRF-TOKEN", token))
            .header("X-XSRF-TOKEN", token)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("username", "tester")
            .param("password", "password"))
        .andExpect(status().isOk())
        .andReturn();

    MockHttpSession session = (MockHttpSession) Objects.requireNonNull(
        loginResult.getRequest().getSession(false), "세션이 생성되지 않았습니다");
    mockMvc.perform(post("/api/auth/logout")
            .session(session)
            .cookie(new Cookie("XSRF-TOKEN", token))
            .header("X-XSRF-TOKEN", token))
        .andExpect(status().isNoContent());
  }

  private String fetchCsrfToken() throws Exception {
    MvcResult result = mockMvc.perform(get("/api/auth/csrf-token"))
        .andExpect(status().isNonAuthoritativeInformation())
        .andReturn();
    Cookie cookie = result.getResponse().getCookie("XSRF-TOKEN");
    return Objects.requireNonNull(cookie, "CSRF 토큰 쿠키가 없습니다").getValue();
  }
}
