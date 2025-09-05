package com.sprint.mission.discodeit.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sprint.mission.discodeit.testconfig.AbstractTestKafkaConfig;
import jakarta.servlet.http.Cookie;
import java.util.Objects;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@Tag("integration")
@ActiveProfiles("security-test")
@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest extends AbstractTestKafkaConfig {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void 인증_없이_API_요청하면_401이_반환된다() throws Exception {
    mockMvc.perform(get("/api/channels"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void CSRF_토큰_발급_API는_203과_쿠키를_반환한다() throws Exception {
    MvcResult result = mockMvc.perform(get("/api/auth/csrf-token"))
        .andExpect(status().isNonAuthoritativeInformation())
        .andReturn();

    Cookie cookie = Objects.requireNonNull(result.getResponse().getCookie("XSRF-TOKEN"),
        "XSRF-TOKEN 쿠키가 없습니다");
    assertThat(cookie.isHttpOnly()).isFalse();
  }
}
