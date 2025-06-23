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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.sprint.mission.discodeit.dto.response.MessageResponse;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.service.MessageService;

@WebMvcTest(MessageController.class)
class MessageControllerTest {

  @Autowired
  MockMvc mockMvc;

  @MockitoBean
  MessageService messageService;

  @Test
  void 채널별_메시지_목록_조회() throws Exception {
    MessageResponse msg1 = new MessageResponse(
        UUID.randomUUID(),
        java.time.Instant.now(),
        java.time.Instant.now(),
        "내용1",
        UUID.randomUUID(),
        null,
        List.of());
    MessageResponse msg2 = new MessageResponse(
        UUID.randomUUID(),
        java.time.Instant.now(),
        java.time.Instant.now(),
        "내용2",
        UUID.randomUUID(),
        null,
        List.of());
    PageResponse<MessageResponse> page = new PageResponse<>(List.of(msg1, msg2), null, 2, false,
        2L);
    when(messageService.findAllByChannelIdWithCursor(any(), any(), any())).thenReturn(page);

    mockMvc.perform(MockMvcRequestBuilders.get("/api/messages")
        .param("channelId", UUID.randomUUID().toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].content").value("내용1"))
        .andExpect(jsonPath("$.content[1].content").value("내용2"));
  }

  @Test
  void 메시지_생성_실패_잘못된_요청() throws Exception {
    MockMultipartFile invalidPart = new MockMultipartFile(
        "messageCreateRequest", null, "application/json", "{}".getBytes());
    mockMvc.perform(MockMvcRequestBuilders.multipart("/api/messages")
        .file(invalidPart)
        .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isBadRequest());
    verifyNoInteractions(messageService);
  }
}
