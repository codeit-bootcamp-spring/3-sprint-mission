package com.sprint.mission.discodeit.slice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.controller.MessageController;
import com.sprint.mission.discodeit.dto.message.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.message.response.AdvancedJpaPageResponse;
import com.sprint.mission.discodeit.dto.message.response.JpaMessageResponse;
import com.sprint.mission.discodeit.dto.user.JpaUserResponse;
import com.sprint.mission.discodeit.exception.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.MessageNotFoundException;
import com.sprint.mission.discodeit.unit.basic.BasicChannelService;
import com.sprint.mission.discodeit.unit.basic.BasicMessageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.*;

import static org.hamcrest.Matchers.hasKey;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * PackageName  : com.sprint.mission.discodeit.slice.controller
 * FileName     : MessageControllerTest
 * Author       : dounguk
 * Date         : 2025. 6. 21.
 */
@WebMvcTest(controllers = MessageController.class)
@DisplayName("Message Controller 슬라이스 테스트")
public class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BasicMessageService messageService;

    @MockitoBean
    private BasicChannelService channelService;

    @Test
    @DisplayName("Cursor가 없어도 채널안에 포함된 메세지들을 가져오는 API가 정상 작동한다.")
    void findMessages_noCursor_success() throws Exception {
        // given
        UUID channelId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());

        JpaMessageResponse message = JpaMessageResponse.builder()
            .id(UUID.randomUUID())
            .channelId(channelId)
            .build();

        List<JpaMessageResponse> messageResponses = Arrays.asList(message);

        AdvancedJpaPageResponse response = AdvancedJpaPageResponse.builder()
            .content(messageResponses)
            .build();

        given(messageService.findAllByChannelIdAndCursor(channelId, null, pageable))
            .willReturn(response);

        // when n then
        mockMvc.perform(get("/api/messages")
                .param("channelId", channelId.toString())
                .param("page", String.valueOf(pageable.getPageNumber()))
                .param("size", String.valueOf(pageable.getPageSize()))
                .param("sort", "createdAt,desc"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isNotEmpty())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content.size()").value(messageResponses.size()));
    }

    @Test
    @DisplayName("cursor가 있을경우 커서 기준 채널안 메세지들을 가져오는 API가 정상 작동한다.")
    void findMessages_cursor_success() throws Exception {
        // given
        UUID channelId = UUID.randomUUID();
        Instant cursor = Instant.now();
        int numberOfMessages = 4;
        Pageable pageable = PageRequest.of(0, 2, Sort.by("createdAt").descending());

        List<JpaMessageResponse> messageResponses = new ArrayList<>();
        for (int i = 0; i < numberOfMessages; i++) {
            JpaMessageResponse message = JpaMessageResponse.builder()
                .id(UUID.randomUUID())
                .content("content" + i)
                .channelId(channelId)
                .createdAt(cursor.minusSeconds(i + 1))
                .build();
            messageResponses.add(message);
        }

        AdvancedJpaPageResponse response = AdvancedJpaPageResponse.builder()
            .content(messageResponses)
            .nextCursor(cursor)
            .size(pageable.getPageSize())
            .hasNext(true)
            .build();

        given(messageService.findAllByChannelIdAndCursor(eq(channelId), eq(cursor), eq(pageable)))
            .willReturn(response);

        // when n then
        mockMvc.perform(get("/api/messages")
                .param("channelId", channelId.toString())
                .param("cursor", cursor.toString())
                .param("page", String.valueOf(pageable.getPageNumber()))
                .param("size", String.valueOf(pageable.getPageSize()))
                .param("sort", "createdAt,desc"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content").isNotEmpty())
            .andExpect(jsonPath("$.content.size()").value(messageResponses.size()))
            .andExpect(jsonPath("$.content[0].channelId").value(channelId.toString()));
    }

    @Test
    @DisplayName("메세지 생성 API가 정상 작동한다.")
    void createMessage_success() throws Exception {
        // given
        UUID channelId = UUID.randomUUID();
        UUID messageId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        MessageCreateRequest request = MessageCreateRequest.builder()
            .channelId(channelId)
            .authorId(userId)
            .content("content")
            .build();

        JpaUserResponse userResponse = JpaUserResponse.builder()
            .id(userId)
            .build();

        JpaMessageResponse response = JpaMessageResponse.builder()
            .id(messageId)
            .content("content")
            .channelId(channelId)
            .author(userResponse)
            .build();

        given(messageService.createMessage(any(MessageCreateRequest.class), any()))
            .willReturn(response);

        MockMultipartFile jsonPart = new MockMultipartFile(
            "messageCreateRequest",
            "",
            MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsBytes(request)
        );

        // when n then
        mockMvc.perform(multipart("/api/messages")
                .file(jsonPart)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(messageId.toString()))
            .andExpect(jsonPath("$.content").value("content"))
            .andExpect(jsonPath("$.channelId").value(channelId.toString()))
            .andExpect(jsonPath("$.author.id").value(userId.toString()));
    }

    @Test
    @DisplayName("메세지 생성시 채널이 없을경우 ChannelNotFoundException(404)를 반환한다.")
    void createMessage_noChannel_ChannelFoundException() throws Exception {
        // given
        UUID channelId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        MessageCreateRequest request = MessageCreateRequest.builder()
            .channelId(channelId)
            .authorId(userId)
            .content("content")
            .build();

        doThrow(new ChannelNotFoundException(Map.of("channelId", channelId)))
            .when(messageService).createMessage(any(MessageCreateRequest.class), any());


        MockMultipartFile jsonPart = new MockMultipartFile(
            "messageCreateRequest",
            "",
            MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsBytes(request)
        );

        // when n then
        mockMvc.perform(multipart("/api/messages")
                .file(jsonPart)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("채널을 찾을 수 없습니다."))
            .andExpect(jsonPath("$.code").value("CHANNEL_NOT_FOUND"))
            .andExpect(jsonPath("$.details").isMap())
            .andExpect(jsonPath("$.details", hasKey("channelId")))
            .andExpect(jsonPath("$.details.channelId").value(channelId.toString()));
    }

    @Test
    @DisplayName("메세지 삭제 API가 정상작동한다.")
    void deleteMessage_success() throws Exception {
        // given
        UUID messageId = UUID.randomUUID();

        // when n then
        mockMvc.perform(delete("/api/messages/{messageId}", messageId))
            .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("삭제할 메세지를 찾지 못할경우 MessageNotFoundException을 반환한다.")
    void deleteMessage_noMessage_MessageNotFoundException() throws Exception {
        // given
        UUID messageId = UUID.randomUUID();

        doThrow(new MessageNotFoundException(Map.of("messageId", messageId)))
            .when(messageService).deleteMessage(any(UUID.class));

        // when n then
        mockMvc.perform(delete("/api/messages/{messageId}", messageId))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("메세지를 찾을 수 없습니다."))
            .andExpect(jsonPath("$.code").value("MESSAGE_NOT_FOUND"))
            .andExpect(jsonPath("$.details").isMap())
            .andExpect(jsonPath("$.details", hasKey("messageId")))
            .andExpect(jsonPath("$.details.messageId").value(messageId.toString()));
    }


    @Test
    @DisplayName("메세지 생성 API가 정상적으로 동작한다.")
    void messageUpdate_success() throws Exception {
        // given
        UUID messageId = UUID.randomUUID();
        MessageUpdateRequest request = new MessageUpdateRequest("new Content");
        JpaMessageResponse response = JpaMessageResponse.builder()
            .id(messageId)
            .content("new Content")
            .build();

        given(messageService.updateMessage(any(UUID.class), any(MessageUpdateRequest.class)))
            .willReturn(response);

        // when n then
        mockMvc.perform(patch("/api/messages/{messageId}", messageId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(messageId.toString()))
            .andExpect(jsonPath("$.content").value("new Content"));
    }


    @Test
    @DisplayName("메세지가 없으면 MessageNotFoundException(404)을 반환한다.")
    void messageUpdate_noMessage_MessageNotFoundException() throws Exception {
        // given
        UUID messageId = UUID.randomUUID();
        MessageUpdateRequest request = new MessageUpdateRequest("new Content");
        given(messageService.updateMessage(any(UUID.class), any(MessageUpdateRequest.class)))
            .willThrow(new MessageNotFoundException(Map.of("messageId", messageId)));

        // when n then
        mockMvc.perform(patch("/api/messages/{messageId}", messageId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("메세지를 찾을 수 없습니다."))
            .andExpect(jsonPath("$.code").value("MESSAGE_NOT_FOUND"))
            .andExpect(jsonPath("$.details").isMap())
            .andExpect(jsonPath("$.details", hasKey("messageId")))
            .andExpect(jsonPath("$.details.messageId").value(messageId.toString()));
    }

}