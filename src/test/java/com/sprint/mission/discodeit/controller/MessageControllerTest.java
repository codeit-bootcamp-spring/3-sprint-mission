package com.sprint.mission.discodeit.controller;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.Message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.Message.MessageDto;
import com.sprint.mission.discodeit.dto.Message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.User.UserDto;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.service.MessageService;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MessageController.class)
@ActiveProfiles("test")
@DisplayName("MessageController 슬라이스 테스트")
public class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MessageService messageService;

    @Test
    @DisplayName("메시지 생성 API가 정상적으로 동작한다.")
    void shouldCreateMessage_whenValidRequest() throws Exception {

        // given
        String content = "메시지 생성 테스트입니다.";
        UUID messageId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        UserDto userDto = new UserDto(userId, "테스트유저", "test@codeit.com", null, true);
        MessageCreateRequest createRequest = new MessageCreateRequest(content, channelId, userId);
        MessageDto messageDto = new MessageDto(messageId, Instant.now(), Instant.now(), content,
                channelId, userDto, new ArrayList<>());

        given(messageService.create(any(MessageCreateRequest.class), anyList())).willReturn(
                messageDto);

        MockMultipartFile jsonPart = new MockMultipartFile(
                "messageCreateRequest",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(createRequest)
        );

        // when & then
        mockMvc.perform(multipart("/api/messages")
                        .file(jsonPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(messageId.toString()))
                .andExpect(jsonPath("$.content").value(content))
                .andExpect(jsonPath("$.channelId").value(channelId.toString()))
                .andExpect(jsonPath("$.author.id").value(userId.toString()));
    }

    @Test
    @DisplayName("유효하지 않은 입력으로 메시지 생성 시 400 에러가 발생한다.")
    void shouldReturnBadRequest_whenInvalidMessageCreateRequest() throws Exception {

        // given
        String content = "";
        UUID messageId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        UserDto userDto = new UserDto(userId, "테스트유저", "test@codeit.com", null, true);
        MessageCreateRequest createRequest = new MessageCreateRequest(content, channelId, userId);
        MessageDto messageDto = new MessageDto(messageId, Instant.now(), Instant.now(), content,
                channelId, userDto, new ArrayList<>());

        given(messageService.create(any(MessageCreateRequest.class), anyList())).willReturn(
                messageDto);

        MockMultipartFile jsonPart = new MockMultipartFile(
                "messageCreateRequest",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(createRequest)
        );

        // when & then
        mockMvc.perform(multipart("/api/messages")
                        .file(jsonPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("메시지 수정 API가 정상적으로 동작한다.")
    void shouldUpdateMessage_whenValidRequest() throws Exception {

        // given
        String newContent = "메시지 수정 테스트입니다.";
        UUID messageId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        UserDto userDto = new UserDto(userId, "테스트유저", "test@codeit.com", null, true);

        MessageUpdateRequest updateRequest = new MessageUpdateRequest(newContent);
        MessageDto messageDto = new MessageDto(messageId, Instant.now(), Instant.now(), newContent,
                channelId, userDto, new ArrayList<>());

        given(messageService.update(messageId, updateRequest)).willReturn(messageDto);

        // when & then
        mockMvc.perform(patch("/api/messages/" + messageId.toString())
                        .content(objectMapper.writeValueAsBytes(updateRequest))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(messageId.toString()))
                .andExpect(jsonPath("$.content").value(newContent));
    }

    @Test
    @DisplayName("유효하지 않은 입력으로 메시지 수정 시 400 에러가 발생한다.")
    void shouldReturnBadRequest_whenInvalidMessageUpdateRequest() throws Exception {

        // given
        String newContent = "";
        UUID messageId = UUID.randomUUID();
        MessageUpdateRequest updateRequest = new MessageUpdateRequest(newContent);

        // when & then
        mockMvc.perform(patch("/api/messages/" + messageId.toString())
                        .content(objectMapper.writeValueAsBytes(updateRequest))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("메시지 삭제 API가 정상적으로 동작한다.")
    void shouldDeleteMessage_whenValidMessageId() throws Exception {

        // given
        UUID messageId = UUID.randomUUID();
        willDoNothing().given(messageService).delete(messageId);

        // when & then
        mockMvc.perform(delete(("/api/messages/" + messageId.toString())))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("특정 채널의 메시지 전체 조회 API가 정상적으로 동작한다.")
    void shouldReturnAllMessages_whenChannelIdIsValid() throws Exception {

        // given
        UUID channelId = UUID.randomUUID();

        Instant now = Instant.now();

        List<MessageDto> messages = List.of(
                new MessageDto(UUID.randomUUID(), now, now, "테스트 메시지 1", channelId, null,
                        List.of()),
                new MessageDto(UUID.randomUUID(), now, now, "테스트 메시지 2", channelId, null, List.of())
        );

        PageResponse<MessageDto> result = new PageResponse<>(
                messages,
                now.plusSeconds(10),
                2,
                true,
                null);

        given(messageService.findAllByChannelId(eq(channelId), any(), any())).willReturn(
                result);

        // when & then
        mockMvc.perform(get("/api/messages").param("channelId", channelId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].content").value("테스트 메시지 1"))
                .andExpect(jsonPath("$.content[1].content").value("테스트 메시지 2"))
                .andExpect(jsonPath("$.hasNext").value(true))
                .andExpect(jsonPath("$.nextCursor").value(now.plusSeconds(10).toString()))
                .andExpect(jsonPath("$.size").value(2));
    }
}
