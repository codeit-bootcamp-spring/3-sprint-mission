package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.exception.binaryContent.InvalidFileProcessingException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.service.MessageService;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@WebMvcTest(MessageController.class)
@Import({GlobalExceptionHandler.class})
class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MessageService messageService;

    @Nested
    @DisplayName("메시지 생성 테스트")
    class CreateMessageTests {

        @Test
        @DisplayName("메시지 생성 성공")
        void createMessage_Success() throws Exception {
            // Given
            UUID channelId = UUID.randomUUID();
            UUID authorID = UUID.randomUUID();
            UserDto userDto = new UserDto(authorID, "martin", "clean@code.com", null, null);

            MessageCreateRequest messageCreateRequest
                = new MessageCreateRequest("클린 코드", channelId, authorID);

            MessageDto messageDto
                = new MessageDto(UUID.randomUUID(), Instant.now(), Instant.now(), "클린 코드",
                channelId, userDto, null);

            given(messageService.create(eq(messageCreateRequest), any())).willReturn(messageDto);

            MockMultipartFile messageCreateRequestFile
                = toJsonPart(messageCreateRequest);

            MockMultipartFile attachmentsPart = fakeImagePart();

            // When & Then
            mockMvc.perform(multipart("/api/messages")
                    .file(messageCreateRequestFile)
                    .file(attachmentsPart)
                    .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("클린 코드"));

        }

        @Test
        @DisplayName("메시지 생성 실패 - 파일 처리 예외")
        void createMessage_Fail_InvalidFile() throws Exception {
            // Given
            UUID channelId = UUID.randomUUID();
            UUID authorID = UUID.randomUUID();

            MessageCreateRequest messageCreateRequest
                = new MessageCreateRequest("클린 코드", channelId, authorID);

            given(messageService.create(eq(messageCreateRequest), any()))
                .willThrow(new InvalidFileProcessingException("Invalid file"));

            MockMultipartFile messageCreateRequestFile
                = toJsonPart(messageCreateRequest);

            MockMultipartFile attachmentsPart = fakeImagePart();

            // When & Then
            mockMvc.perform(multipart("/api/messages")
                    .file(messageCreateRequestFile)
                    .file(attachmentsPart)
                    .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("이미지 처리 중 오류가 발생했습니다."))
                .andExpect(jsonPath("$.details.fileName").value("Invalid file"));

        }
    }

    @Nested
    @DisplayName("메시지 수정 테스트")
    class UpdateMessageTests {

        @Test
        @DisplayName("메시지 수정 성공")
        void updateMessage_Success() throws Exception {
            // Given
            UUID authorID = UUID.randomUUID();
            UUID channelId = UUID.randomUUID();
            UUID messageId = UUID.randomUUID();
            UserDto userDto = new UserDto(authorID, "martin", "clean@code.com", null, null);

            MessageUpdateRequest messageUpdateRequest = new MessageUpdateRequest("수정됨");

            MessageDto messageDto = new MessageDto(messageId, Instant.now(), Instant.now(), "수정됨",
                channelId, userDto, null);

            given(messageService.update(messageId, messageUpdateRequest)).willReturn(messageDto);

            // When & Then
            mockMvc.perform(patch("/api/messages/{id}", messageId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(messageUpdateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("수정됨"));
        }

        @Test
        @DisplayName("메시지 수정 실패 - 메시지 없음")
        void updateMessage_Fail_NotFound() throws Exception {
            // Given
            UUID messageId = UUID.randomUUID();
            MessageUpdateRequest request = new MessageUpdateRequest("수정내용");

            given(messageService.update(eq(messageId), eq(request)))
                .willThrow(new MessageNotFoundException(messageId));

            // When & Then
            mockMvc.perform(patch("/api/messages/{id}", messageId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Message를 찾을 수 없습니다."))
                .andExpect(jsonPath("$.details.messageId").value(messageId.toString()));
        }

    }

    @Nested
    @DisplayName("메시지 삭제 테스트")
    class DeleteMessageTests {

        @Test
        @DisplayName("메시지 삭제 성공")
        void deleteMessage_Success() throws Exception {
            // Given
            UUID messageId = UUID.randomUUID();
            willDoNothing().given(messageService).delete(messageId);

            //When & Then
            mockMvc.perform(delete("/api/messages/{id}", messageId))
                .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("메시지 삭제 실패 - 메시지 없음")
        void deleteMessage_Fail_NotFound() throws Exception {
            // Given
            UUID messageId = UUID.randomUUID();
            willThrow(new MessageNotFoundException(messageId))
                .given(messageService).delete(messageId);

            // When & Then
            mockMvc.perform(delete("/api/messages/{id}", messageId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Message를 찾을 수 없습니다."));
        }
    }


    private MockMultipartFile toJsonPart(Object value) throws Exception {
        return new MockMultipartFile(
            "messageCreateRequest",
            "messageCreateRequest" + ".json",
            "application/json",
            objectMapper.writeValueAsBytes(value)
        );
    }

    private MockMultipartFile fakeImagePart() {
        return new MockMultipartFile(
            "attachments",
            "image.png",
            MediaType.IMAGE_PNG_VALUE,
            "fake-image-content".getBytes()
        );
    }
}
