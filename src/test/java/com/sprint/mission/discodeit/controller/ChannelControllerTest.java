package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelResponse;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.service.ChannelService;

@WebMvcTest(ChannelController.class)
class ChannelControllerTest {
  @Autowired
  MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  ChannelService channelService;

  @Test
  void 전체_채널_목록_조회() throws Exception {
    ChannelResponse ch1 = new ChannelResponse(
        UUID.randomUUID(),
        ChannelType.PUBLIC,
        "채널1",
        "설명1",
        null,
        null);
    ChannelResponse ch2 = new ChannelResponse(
        UUID.randomUUID(),
        ChannelType.PRIVATE,
        "채널2",
        "설명2",
        null,
        null);
    when(channelService.findAllByUserId(any())).thenReturn(List.of(ch1, ch2));

    mockMvc.perform(MockMvcRequestBuilders.get("/api/channels")
        .param("userId", UUID.randomUUID().toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].name").value("채널1"))
        .andExpect(jsonPath("$[1].name").value("채널2"));
  }

  @Test
  void 채널_생성_실패_잘못된_요청() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.post("/api/channels/public")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{}"))
        .andExpect(status().isBadRequest());
    verifyNoInteractions(channelService);
  }

  @Test
  void 공개_채널_생성_성공() throws Exception {
    PublicChannelCreateRequest request = new PublicChannelCreateRequest("채널A", "설명A");
    String requestJson = objectMapper.writeValueAsString(request);
    ChannelResponse expected = new ChannelResponse(
        UUID.randomUUID(), ChannelType.PUBLIC, "채널A", "설명A", null, null);
    when(channelService.create(eq("채널A"), eq("설명A"))).thenReturn(expected);

    mockMvc.perform(MockMvcRequestBuilders.post("/api/channels/public")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestJson))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("채널A"))
        .andExpect(jsonPath("$.description").value("설명A"));
  }

  @Test
  void 비공개_채널_생성_실패_참여자_없음() throws Exception {
    PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(List.of());
    String requestJson = objectMapper.writeValueAsString(request);
    mockMvc.perform(MockMvcRequestBuilders.post("/api/channels/private")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestJson))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorCode").value("INVALID_INPUT_VALUE"));
    verifyNoInteractions(channelService);
  }

  @Test
  void 비공개_채널_생성_성공() throws Exception {
    PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(
        List.of(UUID.randomUUID(), UUID.randomUUID()));
    String requestJson = objectMapper.writeValueAsString(request);
    ChannelResponse expected = new ChannelResponse(
        UUID.randomUUID(), ChannelType.PRIVATE, "비공개채널", null, null, null);
    when(channelService.create(any())).thenReturn(expected);

    mockMvc.perform(MockMvcRequestBuilders.post("/api/channels/private")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestJson))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.type").value("PRIVATE"));
  }

  @Test
  void 채널_수정_성공() throws Exception {
    UUID channelId = UUID.randomUUID();
    PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("수정채널", "수정설명");
    String requestJson = objectMapper.writeValueAsString(request);
    ChannelResponse expected = new ChannelResponse(
        channelId, ChannelType.PUBLIC, "수정채널", "수정설명", null, null);
    when(channelService.update(eq(channelId), eq("수정채널"), eq("수정설명"))).thenReturn(expected);

    mockMvc.perform(MockMvcRequestBuilders.patch("/api/channels/" + channelId)
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestJson))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("수정채널"))
        .andExpect(jsonPath("$.description").value("수정설명"));
  }

  @Test
  void 채널_삭제_성공() throws Exception {
    UUID channelId = UUID.randomUUID();
    mockMvc.perform(MockMvcRequestBuilders.delete("/api/channels/" + channelId))
        .andExpect(status().isNoContent());
    verify(channelService).delete(channelId);
  }
}
