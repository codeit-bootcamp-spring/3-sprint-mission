package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.exception.message.MessageEmptyException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(MessageController.class)
@ActiveProfiles("test")
@DisplayName("MessageController 슬라이스 테스트")
@Import({GlobalExceptionHandler.class})
class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ChannelService channelService;

    @MockitoBean
    private MessageService messageService;

    @Test
    @DisplayName("메시지 생성 - 성공")
    void create_Success() throws Exception {
        // Given
        MessageCreateRequest request = new MessageCreateRequest(
            "테스트메시지", UUID.randomUUID(), UUID.randomUUID()
        );
        MessageDto messageDto = new MessageDto(UUID.randomUUID(), Instant.now(), null,
            request.content(), request.channelId(), null, null);
        String requestJson = objectMapper.writeValueAsString(request);
        MockMultipartFile messagePart = new MockMultipartFile(
            "messageCreateRequest",
            "",
            MediaType.APPLICATION_JSON_VALUE,
            requestJson.getBytes(StandardCharsets.UTF_8)
        );
        given(messageService.create(request, List.of())).willReturn(messageDto);

        // When
        ResultActions result = mockMvc.perform(multipart("/api/messages")
            .file(messagePart)
            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        // Then
        result.andExpect(status().isCreated())
            .andExpect(jsonPath("$.content").value("테스트메시지"))
            .andDo(print());
    }

    @Test
    @DisplayName("메시지 생성 - 실패()")
    void create_Fail() throws Exception {
        // Given
        MessageCreateRequest request = new MessageCreateRequest("", UUID.randomUUID(),
            UUID.randomUUID());
        String requestJson = objectMapper.writeValueAsString(request);
        MockMultipartFile messagePart = new MockMultipartFile(
            "messageCreateRequest",
            "",
            MediaType.APPLICATION_JSON_VALUE,
            requestJson.getBytes()
        );
        given(messageService.create(request, List.of())).willThrow(new MessageEmptyException());

        // When
        ResultActions result = mockMvc.perform(multipart("/api/messages")
            .file(messagePart)
            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        // Then
        result.andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("MESSAGE_EMPTY"))
            .andExpect(jsonPath("$.message").value("메시지 내용과 첨부파일이 모두 비어있습니다."))
            .andDo(print());
    }

    @Test
    @DisplayName("특정 채널에 있는 메시지 전체 조회 - 성공")
    void findAllByChannelId_Success() throws Exception {
        // Given
        UUID channelId = UUID.randomUUID();
        MessageDto message1 = new MessageDto(UUID.randomUUID(), Instant.now(), null, "메시지1",
            channelId, null, null);
        MessageDto message2 = new MessageDto(UUID.randomUUID(), Instant.now(), null, "메시지2",
            channelId, null, null);
        PageResponse<MessageDto> response = new PageResponse<>(List.of(message1, message2), null,
            2, false, null);
        given(messageService.findAllByChannelId(eq(channelId), any(), any())).willReturn(response);

        // When
        ResultActions result = mockMvc.perform(get("/api/messages")
            .param("channelId", channelId.toString()));

        // Then
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.content.length()").value(2))
            .andExpect(jsonPath("$.content[0].content").value("메시지1"))
            .andDo(print());
    }

    @Test
    @DisplayName("메시지 수정 - 성공")
    void update_Success() throws Exception {
        // Given
        UUID messageId = UUID.randomUUID();
        MessageUpdateRequest request = new MessageUpdateRequest("수정된 메시지");
        MessageDto updatedMessage = new MessageDto(messageId, Instant.now(), null, "수정된 메시지",
            UUID.randomUUID(), null, null);
        String json = objectMapper.writeValueAsString(request);
        given(messageService.update(messageId, request)).willReturn(updatedMessage);

        // When
        ResultActions result = mockMvc.perform(patch("/api/messages/{messageId}", messageId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json));

        // Then
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.content").value("수정된 메시지"))
            .andDo(print());
    }

    @Test
    @DisplayName("메시지 수정 - 실패()")
    void update_Fail() throws Exception {
        // Given
        UUID messageId = UUID.randomUUID();
        MessageUpdateRequest request = new MessageUpdateRequest("업데이트 시도");
        String json = objectMapper.writeValueAsString(request);
        given(messageService.update(messageId, request)).willThrow(
            new MessageNotFoundException(messageId));

        // When
        ResultActions result = mockMvc.perform(patch("/api/messages/{messageId}", messageId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json));

        // Then
        result.andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("MESSAGE_NOT_FOUND"))
            .andDo(print());
    }


    @Test
    @DisplayName("메시지 삭제 - 성공")
    void delete_Success() throws Exception {
        // Given
        UUID messageId = UUID.randomUUID();
        willDoNothing().given(messageService).delete(messageId);

        // When
        ResultActions result = mockMvc.perform(delete("/api/messages/{messageId}", messageId));

        // Then
        result.andExpect(status().isNoContent())
            .andDo(print());
    }
}