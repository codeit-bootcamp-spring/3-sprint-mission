package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.MessageResponse;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.service.MessageService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MessageController.class)
@Import(GlobalExceptionHandler.class)
class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MessageService messageService;

    @MockitoBean(name = "jpaMappingContext")
    Object dummyJpaMappingContext;

    @MockitoBean(name = "jpaAuditingHandler")
    Object dummyJpaAuditingHandler;

    @Test
    @DisplayName("메시지 생성 성공")
    void shouldCreateMessageSuccessfully() throws Exception {
        UUID channelId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();

        MessageCreateRequest request = new MessageCreateRequest("안녕하세요", channelId, authorId);

        MessageResponse response = new MessageResponse(
            UUID.randomUUID(),
            Instant.now(),
            Instant.now(),
            "안녕하세요",
            channelId,
            new UserDto(authorId, "tester", "tester@email.com", null, true),
            List.of()
        );

        BDDMockito.given(messageService.create(any(), any())).willReturn(response);

        MockMultipartFile jsonPart = new MockMultipartFile(
            "messageCreateRequest",
            "",
            "application/json",
            objectMapper.writeValueAsBytes(request)
        );

        mockMvc.perform(multipart("/api/messages")
                .file(jsonPart)
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.content").value("안녕하세요"))
            .andExpect(jsonPath("$.channelId").value(channelId.toString()))
            .andExpect(jsonPath("$.author.id").value(authorId.toString()));
    }

    @Test
    @DisplayName("메시지 생성 실패 - 유효성 검사")
    void shouldFailToCreateMessageWithInvalidInput() throws Exception {
        MessageCreateRequest request = new MessageCreateRequest("", null, null);

        MockMultipartFile jsonPart = new MockMultipartFile(
            "messageCreateRequest",
            "",
            "application/json",
            objectMapper.writeValueAsBytes(request)
        );

        mockMvc.perform(multipart("/api/messages")
                .file(jsonPart)
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").exists())
            .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("메시지 업데이트 성공")
    void shouldUpdateMessageSuccessfully() throws Exception {
        UUID messageId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        MessageUpdateRequest request = new MessageUpdateRequest("수정된 메시지");

        MessageResponse response = new MessageResponse(
            messageId,
            Instant.now(),
            Instant.now(),
            "수정된 메시지",
            channelId,
            new UserDto(authorId, "tester", "tester@email.com", null, true),
            List.of()
        );

        BDDMockito.given(messageService.update(messageId, request)).willReturn(response);

        mockMvc.perform(patch("/api/messages/{messageId}", messageId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").value("수정된 메시지"))
            .andExpect(jsonPath("$.channelId").value(channelId.toString()))
            .andExpect(jsonPath("$.author.id").value(authorId.toString()));
    }

    @Test
    @DisplayName("메시지 삭제 성공")
    void shouldDeleteMessageSuccessfully() throws Exception {
        UUID messageId = UUID.randomUUID();
        MessageResponse response = new MessageResponse(
            messageId, Instant.now(), Instant.now(), "삭제된 메시지", UUID.randomUUID(), null, List.of()
        );

        BDDMockito.given(messageService.delete(messageId)).willReturn(response);

        mockMvc.perform(delete("/api/messages/{messageId}", messageId))
            .andExpect(status().isNoContent());
    }


}
