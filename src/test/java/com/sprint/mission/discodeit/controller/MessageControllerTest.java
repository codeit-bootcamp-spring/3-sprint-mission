package com.sprint.mission.discodeit.controller;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.service.MessageService;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MessageController.class)
@ActiveProfiles("test")
@DisplayName("MessageController 테스트")
public class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MessageService messageService;

    @Test
    @DisplayName("메세지 생성 - 성공")
    void createMessage_Success() throws Exception {
        //Given
        MessageCreateRequest request = new MessageCreateRequest(
            "Test Message",
            UUID.randomUUID(),
            UUID.randomUUID()
        );

        MessageDto responseDto = new MessageDto(
            UUID.randomUUID(),
            Instant.now(),
            null,
            request.content(),
            UUID.randomUUID(),
            new UserDto(UUID.randomUUID(), "Test User", "Test Email", null, true),
            null
        );

        MockMultipartFile requestPart = new MockMultipartFile(
            "messageCreateRequest",
            "",
            MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsBytes(request)
        );

        given(messageService.create(eq(request), any(List.class))).willReturn(responseDto);
        //When & Then
        mockMvc.perform(multipart("/api/messages")
                .file(requestPart)
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.content").value("Test Message"));
    }

    @Test
    @DisplayName("메세지 생성 - 유효성 검사 실패")
    void createMessage_ValidationFailure() throws Exception {
        //Given
        MessageCreateRequest request = new MessageCreateRequest(
            "",
            UUID.randomUUID(),
            UUID.randomUUID()
        );

        MessageDto responseDto = new MessageDto(
            UUID.randomUUID(),
            Instant.now(),
            null,
            request.content(),
            UUID.randomUUID(),
            new UserDto(UUID.randomUUID(), "Test User", "Test Email", null, true),
            null
        );

        MockMultipartFile requestPart = new MockMultipartFile(
            "messageCreateRequest",
            "",
            MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsBytes(request)
        );

        given(messageService.create(eq(request), any(List.class))).willReturn(responseDto);
        //When & Then
        mockMvc.perform(multipart("/api/messages")
                .file(requestPart)
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isBadRequest());
    }


    @Test
    @DisplayName("채널별 메시지 조회 - 성공 (커서 포함)")
    void findAllMessagesByChannelId_WithCursor_Success() throws Exception {
        // given
        UUID channelId = UUID.randomUUID();
        Instant cursor = Instant.now();

        UserDto author = new UserDto(UUID.randomUUID(), "user1", "user1@example.com", null, true);

        List<MessageDto> messages = Arrays.asList(
            new MessageDto(
                UUID.randomUUID(),
                cursor.minusSeconds(3600),
                cursor.minusSeconds(3600),
                "Older message",
                channelId,
                author,
                Arrays.asList()
            )
        );

        PageResponse<MessageDto> pageResponse = new PageResponse<MessageDto>(messages,
            Instant.now(), 10,
            true, 1L);

        given(messageService.findAllByChannelId(eq(channelId), eq(cursor), any(Pageable.class)))
            .willReturn(pageResponse);

        // when & then
        mockMvc.perform(get("/api/messages")
                .param("channelId", channelId.toString())
                .param("cursor", cursor.toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content.length()").value(1))
            .andExpect(jsonPath("$.content[0].content").value("Older message"))
            .andExpect(jsonPath("$.hasNext").value(true));
    }


    @Test
    @DisplayName("채널별 메시지 조회 - 실패(channelId 누락)")
    void findAllMessagesByChannelId_MissingParameter() throws Exception {
        
        //when && then
        mockMvc.perform(get("/api/messages"))
            .andExpect(status().isBadRequest());

    }
}
