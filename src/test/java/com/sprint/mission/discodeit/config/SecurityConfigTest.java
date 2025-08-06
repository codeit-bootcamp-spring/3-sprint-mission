package com.sprint.mission.discodeit.config;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("security-test")
@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void 인증_없이_요청하면_리다이렉트된다() throws Exception {
    mockMvc.perform(get("/"))
        .andExpect(status().is3xxRedirection());
  }
}
