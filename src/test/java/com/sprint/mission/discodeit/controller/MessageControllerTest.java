package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.service.MessageService;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


@WebMvcTest(MessageController.class)
@Import(GlobalExceptionHandler.class)
class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MessageService messageService;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID channelId;
    private UUID userId;
    private Instant now;
    private UserDto userDto;

    @BeforeEach
    void setup() {
        channelId = UUID.randomUUID();
        userId = UUID.randomUUID();
        now = Instant.now();
        userDto = new UserDto(userId, "testUser", "test@example.com", null, null);
    }

    @Test
    @DisplayName("메시지 생성 API 성공")
    void createMessage_success() throws Exception {
        // Given
        MessageCreateRequest request = new MessageCreateRequest("testMessage", channelId, userId);
        MessageDto messageDto = new MessageDto(UUID.randomUUID(), now, null, "testMessage",
            channelId, userDto, null);
        MockMultipartFile jsonPart = new MockMultipartFile(
            "messageCreateRequest",
            "",
            "application/json",
            objectMapper.writeValueAsBytes(request)
        );
        when(messageService.create(any(), any())).thenReturn(messageDto);

        // When
        ResultActions result = mockMvc.perform(multipart("/api/messages")
            .file(jsonPart)
            .contentType(MediaType.MULTIPART_FORM_DATA));

        // Then
        result.andExpect(status().isCreated())
            .andExpect(jsonPath("$.content").value("testMessage"))
            .andExpect(jsonPath("$.author.username").value("testUser"));
    }

    @Test
    @DisplayName("메시지 생성 실패 - 필수값 누락")
    void createMessage_fail_validation() throws Exception {
        // Given
        MessageCreateRequest request = new MessageCreateRequest("", null, null);
        MockMultipartFile requestPart = new MockMultipartFile(
            "messageCreateRequest",
            "",
            "application/json",
            objectMapper.writeValueAsBytes(request)
        );

        // When
        ResultActions result = mockMvc.perform(multipart("/api/messages")
            .file(requestPart)
            .contentType(MediaType.MULTIPART_FORM_DATA));

        // Then
        result.andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
    }

    @Test
    @DisplayName("메시지 수정 성공")
    void updateMessageSuccess() throws Exception {
        // Given
        UUID messageId = UUID.randomUUID();
        MessageUpdateRequest request = new MessageUpdateRequest("수정된 내용");
        MessageDto responseDto = new MessageDto(messageId, now, null, "수정된 내용", channelId, userDto,
            null);
        when(messageService.update(any(), any())).thenReturn(responseDto);

        // When
        ResultActions result = mockMvc.perform(
            MockMvcRequestBuilders.patch("/api/messages/{messageId}", messageId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // Then
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(messageId.toString()))
            .andExpect(jsonPath("$.content").value("수정된 내용"));
    }

    @Test
    @DisplayName("메시지 삭제 성공")
    void deleteMessageSuccess() throws Exception {
        // Given
        UUID messageId = UUID.randomUUID();

        // When
        ResultActions result = mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/messages/{messageId}", messageId));

        // Then
        result.andExpect(status().isNoContent());
    }


}