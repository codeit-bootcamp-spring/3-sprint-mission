package com.sprint.mission.discodeit.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import java.util.List;
import java.util.UUID;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ChannelApiIntegrationTest {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;

  @Test
  @DisplayName("비공개 채널 생성-> 사용자 기준 채널 전체 조회")
  void createPrivateChannel_thenFindAll() throws Exception {
    // given
    UUID ownerId = UUID.fromString("00000000-0000-0000-0000-000000000010");
    List<UUID> participantIds = List.of(ownerId);
    PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(participantIds);

    // when&then
    mockMvc.perform(post("/api/channels/private")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andDo(result -> {
          System.out.println(result.getResponse().getContentAsString());
        })
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.type").value("PRIVATE"));

    mockMvc.perform(get("/api/channels")
            .param("userId", ownerId.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[1].type", Matchers.equalToIgnoringCase("PRIVATE")));
  }

  @Test
  @DisplayName("공개 채널 수정 실패 - 존재하지 않는 ID")
  void updateNonexistentChannel_thenReturnNotFound() throws Exception {
    // Given
    UUID fakeId = UUID.randomUUID();
    PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("Nope", "Still Nope");

    // When & Then
    mockMvc.perform(patch("/api/channels/" + fakeId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound());
  }
}
