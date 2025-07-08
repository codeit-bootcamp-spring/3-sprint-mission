package com.sprint.mission.discodeit.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import java.util.UUID;
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
public class MessageApiIntegrationTest {

  @Autowired
  MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

  @Autowired
  ChannelRepository channelRepository;

  @Test
  @DisplayName("메시지 생성 → 수정 → 삭제 → 조회")
  void createUpdateDeleteFindMessageFlow() throws Exception {
    UUID userId = UUID.fromString("00000000-0000-0000-0000-000000000010");
    // Given: 채널 생성
    Channel channel = new Channel(ChannelType.PUBLIC, null, null);
    channelRepository.save(channel);

    // 메시지 생성 요청 준비
    MessageCreateRequest createRequest = new MessageCreateRequest("Hello world", userId,
        channel.getId());
    MockMultipartFile messagePart = new MockMultipartFile(
        "messageCreateRequest",
        null,
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(createRequest));

    // When: 메시지 생성 요청 수행
    String createResponseJson = mockMvc.perform(multipart("/api/messages")
            .file(messagePart)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated())
        .andReturn().getResponse().getContentAsString();

    MessageDto created = objectMapper.readValue(createResponseJson, MessageDto.class);
    UUID messageId = created.id();

    // Then: 메시지 수정 요청
    MessageUpdateRequest updateRequest = new MessageUpdateRequest("Updated content");
    mockMvc.perform(patch("/api/messages/{messageId}", messageId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").value("Updated content"));

    // 메시지 삭제
    mockMvc.perform(delete("/api/messages/{messageId}", messageId))
        .andExpect(status().isNoContent());

    // 메시지 목록 조회 → 삭제된 메시지 없어야 함
    String findResponse = mockMvc.perform(get("/api/messages")
            .param("channelId", channel.getId().toString()))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();

    PageResponse<?> response = objectMapper.readValue(findResponse, PageResponse.class);
    assertThat(response.content()).isEmpty();
  }
}
