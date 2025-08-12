package com.sprint.mission.discodeit.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
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
        .andExpect(jsonPath("$.id").value(user.getId().toString()))
        .andExpect(jsonPath("$.username").value("tester"))
        .andExpect(jsonPath("$.email").value("test@test.com"));
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

  @Test
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

    mockMvc.perform(
            get("/api/auth/me").session((MockHttpSession) loginResult.getRequest().getSession(false)))
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

  @Test
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
