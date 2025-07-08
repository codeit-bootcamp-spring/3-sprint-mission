package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.config.JpaConfig;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponseDto;
import com.sprint.mission.discodeit.dto.message.MessageRequestDto;
import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MessageController.class,
        excludeAutoConfiguration = {JpaConfig.class})
@DisplayName("MessageController 슬라이스 테스트")
class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BasicMessageService messageService;

    @Test
    @DisplayName("메시지 생성 요청이 올바르게 처리되어야 한다.")
    void givenValidUserRequest_whenCreateUser_thenReturnCreatedUserResponse() throws Exception {

        // given
        UUID channelId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        UUID messageId = UUID.randomUUID();

        UserResponseDto author = new UserResponseDto(authorId, "test", "test@test.com",
                null, true);

        MessageRequestDto request = new MessageRequestDto("Hello", channelId, authorId);
        BinaryContentResponseDto file = new BinaryContentResponseDto(UUID.randomUUID(), "image.png", 3L,
                "image/png");
        MessageResponseDto expectedResponse = new MessageResponseDto(messageId, Instant.now(), Instant.now(),
                "Hello", channelId, author, List.of(file));

        MockMultipartFile messageCreateRequest = new MockMultipartFile(
                "messageCreateRequest",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(request)
        );

        // 가상의 파일 첨부
        MockMultipartFile attachment = new MockMultipartFile(
                "attachments",
                "image.png",
                "image/png",
                new byte[]{1, 2, 3}
        );

        given(messageService.create(any(MessageRequestDto.class), any()))
                .willReturn(expectedResponse);

        // when
        ResultActions result = mockMvc.perform(multipart("/api/messages")
                .file(messageCreateRequest)
                .file(attachment)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        // then
        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(messageId.toString()))
                .andExpect(jsonPath("$.content").value("Hello"))
                .andExpect(jsonPath("$.author.id").value(authorId.toString()))
                .andExpect(jsonPath("$.channelId").value(channelId.toString()))
                .andExpect(jsonPath("$.attachments[0].fileName").value("image.png"))
                .andExpect(jsonPath("$.attachments[0].contentType").value("image/png"))
                .andExpect(jsonPath("$.attachments[0].size").value(3));
        verify(messageService).create(argThat(req ->
                req.authorId().equals(authorId) &&
                        req.channelId().equals(channelId)
        ), any());
    }

    @Test
    @DisplayName("필수 항목이 누락되면 400 Bad Request가 발생해야 한다.")
    void givenMissingRequiredField_whenCreateMessage_thenReturnBadRequestWithValidationError()
            throws Exception {

        // given - 사용자 ID 누락
        MessageRequestDto request = new MessageRequestDto("Hello", UUID.randomUUID(), null);

        MockMultipartFile messageCreateRequest = new MockMultipartFile(
                "messageCreateRequest",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(request)
        );

        // when
        ResultActions result = mockMvc.perform(multipart("/api/messages")
                .file(messageCreateRequest)
                .contentType(MediaType.MULTIPART_FORM_DATA));

        // then
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.message").value("유효성 검사 실패"));
    }
}