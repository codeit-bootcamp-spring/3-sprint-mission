package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(MessageController.class)
@DisplayName("MessageController 슬라이스 테스트")
public class MessageControllerTest {

    @Autowired MockMvc mockMvc;

    @Autowired ObjectMapper objectMapper;

    @MockitoBean private MessageService messageService;

    @Test
    @DisplayName("메세지 생성 API가 정상적으로 동작 한다")
    void createMessage_Success() throws Exception {
        // given
        UUID channelId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        MessageCreateRequest messageCreateRequest = new MessageCreateRequest("test", channelId, userId);

        MessageDto messageDto = new MessageDto(
                UUID.randomUUID(),
                Instant.now(),
                Instant.now(),
                "test",
                channelId,
                new UserDto(userId, "tester", "test@gmail.com", null, null),
                null
        );

        given(messageService.create(any(MessageCreateRequest.class), any())).willReturn(messageDto);

        MockMultipartFile requestPart = new MockMultipartFile(
                "messageCreateRequest",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(messageCreateRequest)
        );

        // when & then
        mockMvc.perform(multipart("/api/messages")
                        .file(requestPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("test"))
                .andExpect(jsonPath("$.channelId").value(channelId.toString()))
                .andExpect(jsonPath("$.author.id").value(userId.toString()));
    }

    @Test
    @DisplayName("메세지 생성 API - 필드 유효성 실패로 400 반환")
    void createMessage_Fail_InvalidContent() throws Exception {
        // given - userId가 비어 있는 경우
        UUID channelId = UUID.randomUUID();
        UUID userId = null;
        MessageCreateRequest messageCreateRequest = new MessageCreateRequest("test", channelId, userId);

        MockMultipartFile requestPart = new MockMultipartFile(
                "messageCreateRequest",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(messageCreateRequest)
        );

        // when & then
        mockMvc.perform(multipart("/api/messages")
                        .file(requestPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest());
    }
}
