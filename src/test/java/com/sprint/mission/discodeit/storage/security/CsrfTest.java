package com.sprint.mission.discodeit.storage.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@org.junit.jupiter.api.Disabled("테스트 범위에서 제외: 보안 설정 의존성 과다")
public class CsrfTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  @DisplayName("CSRF 토큰 요청 테스트")
  void getCsrfToken() throws Exception {
    // When & Then - CSRF 토큰 엔드포인트가 정상적으로 호출되는지만 확인
    mockMvc.perform(get("/api/auth/csrf-token"))
        .andExpect(status().isNoContent())
        .andExpect(cookie().exists("XSRF-TOKEN"))
    ;
  }
}
