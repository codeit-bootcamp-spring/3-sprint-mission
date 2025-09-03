package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.response.MessageResponse;
import com.sprint.mission.discodeit.entity.Role;
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
import org.springframework.security.test.context.support.WithMockUser;
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
    @WithMockUser(username = "tester", roles = {"USER"})
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
            new UserDto(authorId, "tester", "tester@email.com", null, true, Role.USER),
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
                .with(csrf())
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.content").value("안녕하세요"))
            .andExpect(jsonPath("$.channelId").value(channelId.toString()))
            .andExpect(jsonPath("$.author.id").value(authorId.toString()));
    }

    @Test
    @DisplayName("메시지 생성 실패 - 유효성 검사")
    @WithMockUser
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
                .with(csrf())
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").exists())
            .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("채널 ID 없이 메시지 생성 시 400 반환")
    @WithMockUser
    void shouldReturn400WhenChannelIdMissing() throws Exception {
        MessageCreateRequest invalidRequest = new MessageCreateRequest("유효하지 않은 요청", null,
            UUID.randomUUID());

        MockMultipartFile jsonPart = new MockMultipartFile(
            "messageCreateRequest",
            "",
            "application/json",
            objectMapper.writeValueAsBytes(invalidRequest)
        );

        mockMvc.perform(multipart("/api/messages")
                .file(jsonPart)
                .with(csrf())
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").exists())
            .andExpect(jsonPath("$.code").exists());
    }


}
