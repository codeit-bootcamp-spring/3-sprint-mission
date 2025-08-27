package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.security.jwt.JwtAuthenticationFilter;
import com.sprint.mission.discodeit.service.MessageService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@WebMvcTest(
    controllers = MessageController.class,
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = GlobalExceptionHandler.class
    )
)
@WithMockUser
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("MessageController 슬라이스 테스트")
public class MessageControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    MessageService messageService;

    @MockitoBean
    JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    @DisplayName("메시지 생성 API")
    void createMessage() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        UUID messageId = UUID.randomUUID();

        Instant createdAt = Instant.parse("2025-06-29T10:15:30Z");
        Instant updatedAt = Instant.parse("2025-06-29T10:16:30Z");

        MessageCreateRequest req = new MessageCreateRequest("hello", channelId, userId);
        BinaryContentDto attachDto = new BinaryContentDto(
            UUID.randomUUID(), "test.txt", 5L, "text/plain"
        );
        UserDto userDto = new UserDto(
            userId, "tom", "tom@test.com", null, false, Role.USER
        );
        MessageDto messageDto = new MessageDto(
            messageId,
            Instant.MIN,
            Instant.MIN,
            "hello",
            channelId,
            userDto,
            List.of(attachDto)
        );

        given(messageService.create(any(MessageCreateRequest.class), anyList())).willReturn(messageDto);

        MockMultipartFile jsonPart = new MockMultipartFile(
            "messageCreateRequest",
            "",
            MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsBytes(req)
        );
        MockMultipartFile filePart = new MockMultipartFile(
            "attachments",
            "test.txt",
            "text/plain",
            "hello".getBytes()
        );

        // when, then
        mockMvc.perform(multipart("/api/messages")
                .file(jsonPart)
                .file(filePart)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .with(csrf()))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(messageId.toString()))
            .andExpect(jsonPath("$.content").value(messageDto.content()))
            .andExpect(jsonPath("$.attachments[0].fileName").value(attachDto.fileName()));

        then(messageService).should(times(1))
            .create(any(MessageCreateRequest.class), anyList());
    }

    @Test
    @DisplayName("메시지 수정 API")
    void updateMessage() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        UUID messageId = UUID.randomUUID();
        MessageUpdateRequest req = new MessageUpdateRequest("update content");

        UserDto userDto = new UserDto(
            userId, "tom", "tom@test.com", null, false, Role.USER
        );
        MessageDto messageDto = new MessageDto(
            messageId,
            Instant.MIN,
            Instant.MIN,
            "update content",
            channelId,
            userDto,
            null
        );
        given(messageService.update(messageId, req)).willReturn(messageDto);

        // when, then
        mockMvc.perform(patch("/api/messages/{messageId}", messageId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(messageId.toString()))
            .andExpect(jsonPath("$.content").value(messageDto.content()));

        then(messageService).should(times(1))
            .update(messageId, req);
    }

    @Test
    @DisplayName("메시지 삭제 API")
    void deleteMessage() throws Exception {
        // given
        UUID messageId = UUID.randomUUID();
        willDoNothing().given(messageService).delete(messageId);

        // when, then
        mockMvc.perform(delete("/api/messages/{messageId}", messageId)
                .with(csrf()))
            .andExpect(status().isNoContent());

        then(messageService).should(times(1)).delete(messageId);
    }
}
