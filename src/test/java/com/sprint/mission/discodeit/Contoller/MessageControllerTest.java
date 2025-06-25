package com.sprint.mission.discodeit.Contoller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.controller.MessageController;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.exception.Common.ResourceNotFoundException;
import com.sprint.mission.discodeit.service.MessageService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MessageController.class)
class MessageControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private MessageService messageService;

  @Nested
  @DisplayName("메시지 생성 테스트")
  class CreateMessage {

    @Test
    @DisplayName("첨부파일 없이 메시지 생성 성공")
    void whenCreateMessageWithoutAttachments_thenReturnCreated() throws Exception {
      UUID channelId = UUID.randomUUID();

      MessageCreateRequest request = new MessageCreateRequest(
          "hello!", UUID.randomUUID(), channelId);
      UserDto userDto = new UserDto(UUID.randomUUID(), "test", "test@mail.com",
          null, true,
          Instant.now(), Instant.now());
      MessageDto createdDto = new MessageDto(UUID.randomUUID(), Instant.now(), Instant.now(),
          request.content(),
          channelId, userDto, List.of());

      MockMultipartFile messagePart = new MockMultipartFile(
          "messageCreateRequest", null,
          MediaType.APPLICATION_JSON_VALUE,
          objectMapper.writeValueAsBytes(request)
      );

      given(messageService.create(eq(request), any())).willReturn(createdDto);

      mockMvc.perform(multipart("/api/messages")
              .file(messagePart)
              .contentType(MediaType.MULTIPART_FORM_DATA))
          .andDo(print())
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.content").value("hello!"));
    }

    @Test
    @DisplayName("첨부파일 포함 메시지 생성 실패 시 500 반환")
    void whenCreateMessageFails_thenReturnError() throws Exception {
      MessageCreateRequest request = new MessageCreateRequest(
          "hello!", UUID.randomUUID(), UUID.randomUUID());

      MockMultipartFile messagePart = new MockMultipartFile(
          "messageCreateRequest", null,
          MediaType.APPLICATION_JSON_VALUE,
          objectMapper.writeValueAsBytes(request)
      );

      given(messageService.create(eq(request), any())).willThrow(new RuntimeException("error"));

      mockMvc.perform(multipart("/api/messages")
              .file(messagePart)
              .contentType(MediaType.MULTIPART_FORM_DATA))
          .andExpect(status().isInternalServerError());
    }
  }

  @Nested
  @DisplayName("메시지 수정 테스트")
  class UpdateMessage {

    @Test
    @DisplayName("메시지 수정 성공")
    void whenUpdateMessage_thenReturnUpdatedDto() throws Exception {
      UUID messageId = UUID.randomUUID();
      MessageUpdateRequest request = new MessageUpdateRequest("updated content");
      UserDto userDto = new UserDto(UUID.randomUUID(), "test", "test@mail.com",
          null, true,
          Instant.now(), Instant.now());
      MessageDto updatedDto = new MessageDto(messageId, Instant.now(), Instant.now(),
          request.newContent(), UUID.randomUUID(),
          userDto, null);

      given(messageService.update(messageId, request)).willReturn(updatedDto);

      mockMvc.perform(patch("/api/messages/{messageId}", messageId)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsBytes(request)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.content").value("updated content"));
    }

    @Test
    @DisplayName("존재하지 않는 메시지 수정 시 404 반환")
    void whenUpdateMessageFails_thenReturnNotFound() throws Exception {
      UUID messageId = UUID.randomUUID();
      MessageUpdateRequest request = new MessageUpdateRequest("any");

      given(messageService.update(messageId, request))
          .willThrow(new ResourceNotFoundException("messageId = " + messageId));

      mockMvc.perform(patch("/api/messages/{messageId}", messageId)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsBytes(request)))
          .andExpect(status().isNotFound());
    }
  }
}
