package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.service.MessageService;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Pageable;
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

    private UUID messageId;
    private UUID channelId;
    private UUID authorId;

    private MessageCreateRequest messageCreateRequest;
    private MessageUpdateRequest messageUpdateRequest;
    private MessageDto message1;
    private UserDto author;
    private MockMultipartFile attachmentFile;

    @BeforeEach
    void setUp() {
        Instant now = Instant.now();
        Instant createdAt = now.minus(10, ChronoUnit.MINUTES);
        Instant updatedAt = now.minus(10, ChronoUnit.MINUTES);

        messageId = UUID.randomUUID();
        channelId = UUID.randomUUID();
        authorId = UUID.randomUUID();

        messageCreateRequest = new MessageCreateRequest("hello", channelId, authorId);
        messageUpdateRequest = new MessageUpdateRequest("new Content");

        attachmentFile = new MockMultipartFile(
            "첨부파일", "image.png", MediaType.IMAGE_JPEG_VALUE, "이미지".getBytes()
        );

        author = new UserDto(authorId, "testuser", "test@abc.com", null, true);

        message1 = new MessageDto(messageId, createdAt, updatedAt, "hello", channelId, author,
            List.of());
    }

    private MockMultipartFile toMultipartJson(String name, Object value) throws Exception {
        return new MockMultipartFile(
            name, "", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(value)
        );
    }

    @Test
    @DisplayName("메시지 생성 성공")
    void createMessage_Success() throws Exception {
        given(messageService.createMessage(any(MessageCreateRequest.class),
            any(List.class))).willReturn(message1);

        mockMvc.perform(multipart("/api/messages")
                .file(toMultipartJson("messageCreateRequest", messageCreateRequest))
                .file(attachmentFile)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(message1.id().toString()))
            .andExpect(jsonPath("$.content").value("hello"))
            .andExpect(jsonPath("$.channelId").value(channelId.toString()))
            .andExpect(jsonPath("$.author.id").value(authorId.toString()));
    }

    @Test
    @DisplayName("메시지 생성 실패")
    void createMessage_Failure() throws Exception {
        MessageCreateRequest invalidRequest = new MessageCreateRequest("", channelId, authorId);

        mockMvc.perform(multipart("/api/messages")
                .file(toMultipartJson("messageCreateRequest", invalidRequest))
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("메시지 수정 성공")
    void updateMessage_Success() throws Exception {
        MessageDto updatedMessage = new MessageDto(messageId, Instant.now(), Instant.now(),
            "updated content", channelId, author, List.of());

        given(messageService.updateMessage(eq(messageId),
            any(MessageUpdateRequest.class))).willReturn(updatedMessage);

        mockMvc.perform(patch("/api/messages/{messageId}", messageId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(messageUpdateRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(messageId.toString()))
            .andExpect(jsonPath("$.content").value("updated content"));
    }

    @Test
    @DisplayName("메시지 수정 실패")
    void updateMessage_Failure() throws Exception {
        UUID nonExistentMessageId = UUID.randomUUID();

        given(
            messageService.updateMessage(eq(nonExistentMessageId), any(MessageUpdateRequest.class)))
            .willThrow(MessageNotFoundException.withId(nonExistentMessageId));

        mockMvc.perform(patch("/api/messages/{messageId}", nonExistentMessageId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(messageUpdateRequest)))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("메시지 삭제 성공")
    void deleteMessage_Success() throws Exception {
        willDoNothing().given(messageService).deleteMessage(UUID.randomUUID(), authorId);

        mockMvc.perform(delete("/api/messages/{messageId}", messageId)
                .param("senderId", authorId.toString())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("메시지 삭제 실패")
    void deleteMessage_Failure() throws Exception {
        UUID nonExistentMessageId = UUID.randomUUID();
        willThrow(MessageNotFoundException.withId(nonExistentMessageId))
            .given(messageService).deleteMessage(nonExistentMessageId, authorId);

        mockMvc.perform(delete("/api/messages/{messageId}", nonExistentMessageId)
                .param("senderId", authorId.toString())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("채널 내 메시지 목록 조회 성공")
    void findAllMessagesInChannel_Success() throws Exception {
        Instant now = Instant.now();
        Instant createdAt = now.minus(5, ChronoUnit.MINUTES);
        Instant updatedAt = now.minus(5, ChronoUnit.MINUTES);

        MessageDto message2 = new MessageDto(UUID.randomUUID(), createdAt, updatedAt, "hi",
            channelId, author, List.of());

        PageResponse<MessageDto> page = new PageResponse<>(
            List.of(message2, message1),
            message1.createdAt(),
            2,
            false,
            2L
        );

        given(messageService.findAllByChannelId(eq(channelId), eq(now),
            any(Pageable.class))).willReturn(page);

        mockMvc.perform(get("/api/messages")
                .param("channelId", channelId.toString())
                .param("cursor", now.toString())
                .param("page", "0")
                .param("size", "50")
                .param("sort", "createdAt,DESC"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].id").value(message2.id().toString()))
            .andExpect(jsonPath("$.content[0].content").value("hi"))
            .andExpect(jsonPath("$.content[1].content").value("hello"))
            .andExpect(jsonPath("$.hasNext").value(false))
            .andExpect(jsonPath("$.totalElements").value(2));
    }

}