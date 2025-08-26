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
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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

    MvcResult result = mockMvc.perform(post("/api/auth/login")
            .cookie(new Cookie("XSRF-TOKEN", token))
            .header("X-XSRF-TOKEN", token)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("username", "tester")
            .param("password", "password"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userDto.id").value(user.getId().toString()))
        .andExpect(jsonPath("$.userDto.username").value("tester"))
        .andExpect(jsonPath("$.userDto.email").value("test@test.com"))
        .andExpect(jsonPath("$.accessToken").isNotEmpty())
        .andReturn();

    Cookie refreshCookie = result.getResponse().getCookie("REFRESH_TOKEN");
    Objects.requireNonNull(refreshCookie, "리프레시 토큰 쿠키가 없습니다");
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
  void 리프레시_토큰으로_액세스_토큰을_재발급한다() throws Exception {
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

    Cookie refreshCookie = Objects.requireNonNull(
        loginResult.getResponse().getCookie("REFRESH_TOKEN"), "REFRESH_TOKEN 쿠키가 없습니다");

    String csrf = fetchCsrfToken();
    mockMvc.perform(post("/api/auth/refresh")
            .cookie(new Cookie("XSRF-TOKEN", csrf))
            .header("X-XSRF-TOKEN", csrf)
            .cookie(refreshCookie))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userDto.id").value(user.getId().toString()))
        .andExpect(jsonPath("$.accessToken").isNotEmpty());
  }

  @Test
  void 잘못된_리프레시_토큰으로_재발급_요청시_401() throws Exception {
    String csrf = fetchCsrfToken();
    mockMvc.perform(post("/api/auth/refresh")
            .cookie(new Cookie("XSRF-TOKEN", csrf))
            .header("X-XSRF-TOKEN", csrf)
            .cookie(new Cookie("REFRESH_TOKEN", "invalid")))
        .andExpect(status().isUnauthorized());
  }

  private String fetchCsrfToken() throws Exception {
    MvcResult result = mockMvc.perform(get("/api/auth/csrf-token"))
        .andExpect(status().isNonAuthoritativeInformation())
        .andReturn();
    Cookie cookie = result.getResponse().getCookie("XSRF-TOKEN");
    return Objects.requireNonNull(cookie, "CSRF 토큰 쿠키가 없습니다").getValue();
  }
}
