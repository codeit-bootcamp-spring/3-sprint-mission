package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.service.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * MessageController에 대한 슬라이스 테스트 클래스입니다.
 * <p>
 * 컨트롤러 레이어만을 독립적으로 테스트하며, 서비스 레이어는 Mock으로 대체하여 HTTP 요청/응답 처리 로직을 검증합니다.
 * <ul>
 *   <li>메시지 생성, 수정, 삭제, 조회 등 다양한 시나리오를 검증합니다.</li>
 * </ul>
 */
@WebMvcTest(MessageController.class)
@Import(GlobalExceptionHandler.class)
class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MessageService messageService;

    private UUID testMessageId;
    private UUID testChannelId;
    private UUID testAuthorId;
    private MessageDto testMessageDto;
    private UserDto testAuthorDto;

    /**
     * 각 테스트 메소드 실행 전에 공통으로 사용할 테스트 데이터를 초기화합니다.
     */
    @BeforeEach
    void setUp() {
        testMessageId = UUID.randomUUID();
        testChannelId = UUID.randomUUID();
        testAuthorId = UUID.randomUUID();
        
        testAuthorDto = new UserDto(
            testAuthorId,
            "testuser",
            "test@example.com",
            null,
            true
        );
        
        testMessageDto = new MessageDto(
            testMessageId,
            Instant.now(),
            Instant.now(),
            "테스트 메시지 내용",
            testChannelId,
            testAuthorDto,
            null
        );
    }

    @Nested
    @DisplayName("메시지 생성 테스트")
    class CreateMessageTest {

        /**
         * [성공] 유효한 메시지 정보로 메시지를 생성할 수 있는지 검증합니다.
         */
        @Test
        @DisplayName("[성공] 유효한 메시지 정보로 메시지 생성")
        void shouldCreateMessageSuccessfully_whenValidMessageDataProvided() throws Exception {
            // given: 유효한 메시지 생성 요청 데이터 준비
            MessageCreateRequest createRequest = new MessageCreateRequest(
                "테스트 메시지 내용",
                testChannelId,
                testAuthorId
            );
            
            given(messageService.create(any(MessageCreateRequest.class), any()))
                .willReturn(testMessageDto);

            // when & then: POST 요청을 보내고 응답 검증
            mockMvc.perform(multipart("/api/messages")
                    .file(new MockMultipartFile(
                        "messageCreateRequest",
                        null,
                        MediaType.APPLICATION_JSON_VALUE,
                        objectMapper.writeValueAsBytes(createRequest)
                    ))
                    .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(testMessageId.toString()))
                .andExpect(jsonPath("$.channelId").value(testChannelId.toString()))
                .andExpect(jsonPath("$.content").value("테스트 메시지 내용"));
        }

        /**
         * [성공] 첨부파일과 함께 메시지를 생성할 수 있는지 검증합니다.
         */
        @Test
        @DisplayName("[성공] 첨부파일과 함께 메시지 생성")
        void shouldCreateMessageWithAttachments_whenAttachmentsProvided() throws Exception {
            // given: 첨부파일이 포함된 메시지 생성 요청 데이터 준비
            MessageCreateRequest createRequest = new MessageCreateRequest(
                "첨부파일이 있는 메시지",
                testChannelId,
                testAuthorId
            );
            
            MockMultipartFile attachment = new MockMultipartFile(
                "attachments",
                "test-image.jpg",
                "image/jpeg",
                "test image content".getBytes()
            );
            
            given(messageService.create(any(MessageCreateRequest.class), any()))
                .willReturn(testMessageDto);

            // when & then: 첨부파일과 함께 POST 요청을 보내고 응답 검증
            mockMvc.perform(multipart("/api/messages")
                    .file(new MockMultipartFile(
                        "messageCreateRequest",
                        null,
                        MediaType.APPLICATION_JSON_VALUE,
                        objectMapper.writeValueAsBytes(createRequest)
                    ))
                    .file(attachment)
                    .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(testMessageId.toString()));
        }

        /**
         * [실패] 잘못된 메시지 정보로 메시지 생성 시 400 Bad Request가 반환되는지 검증합니다.
         */
        @Test
        @DisplayName("[실패] 잘못된 메시지 정보로 메시지 생성 시 400 반환")
        void shouldReturnBadRequest_whenInvalidMessageDataProvided() throws Exception {
            // given: 유효하지 않은 메시지 생성 요청 데이터 준비 (메시지 내용 누락)
            MessageCreateRequest invalidRequest = new MessageCreateRequest(
                "",  // 빈 메시지 내용
                testChannelId,
                testAuthorId
            );

            // when & then: 유효하지 않은 데이터로 POST 요청을 보내고 400 응답 검증
            mockMvc.perform(multipart("/api/messages")
                    .file(new MockMultipartFile(
                        "messageCreateRequest",
                        null,
                        MediaType.APPLICATION_JSON_VALUE,
                        objectMapper.writeValueAsBytes(invalidRequest)
                    ))
                    .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print())
                .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("메시지 수정 테스트")
    class UpdateMessageTest {

        @Test
        @DisplayName("유효한 메시지 정보로 메시지를 수정하면 200 OK와 함께 수정된 메시지 정보를 반환한다")
        void shouldUpdateMessageSuccessfully_whenValidMessageDataProvided() throws Exception {
            // given: 유효한 메시지 수정 요청 데이터 준비
            MessageUpdateRequest updateRequest = new MessageUpdateRequest(
                "수정된 메시지 내용"
            );
            
            MessageDto updatedMessageDto = new MessageDto(
                testMessageId,
                Instant.now(),
                Instant.now(),
                "수정된 메시지 내용",
                testChannelId,
                testAuthorDto,
                null
            );
            
            given(messageService.update(eq(testMessageId), any(MessageUpdateRequest.class)))
                .willReturn(updatedMessageDto);

            // when & then: PATCH 요청을 보내고 응답 검증
            mockMvc.perform(patch("/api/messages/" + testMessageId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testMessageId.toString()))
                .andExpect(jsonPath("$.content").value("수정된 메시지 내용"));
        }

        @Test
        @DisplayName("존재하지 않는 메시지 ID로 수정을 요청하면 MESSAGE_NOT_FOUND 상태코드로 응답한다")
        void shouldThrowException_whenMessageNotFound() throws Exception {
            // given: 존재하지 않는 메시지 ID와 유효한 수정 요청 데이터 준비
            UUID nonExistentMessageId = UUID.randomUUID();
            MessageUpdateRequest updateRequest = new MessageUpdateRequest(
                "수정된 메시지 내용"
            );
            
            given(messageService.update(eq(nonExistentMessageId), any(MessageUpdateRequest.class)))
                .willThrow(new com.sprint.mission.discodeit.exception.message.MessageNotFoundException("메시지를 찾을 수 없습니다."));

            // when & then: 존재하지 않는 메시지 ID로 PATCH 요청을 보내고 예외 응답 검증
            mockMvc.perform(patch("/api/messages/" + nonExistentMessageId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateRequest)))
                .andDo(print())
                .andExpect(status().is(com.sprint.mission.discodeit.exception.ErrorCode.MESSAGE_NOT_FOUND.getStatus().value()))
                .andExpect(jsonPath("$.code").value(com.sprint.mission.discodeit.exception.ErrorCode.MESSAGE_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.message").value(com.sprint.mission.discodeit.exception.ErrorCode.MESSAGE_NOT_FOUND.getMsg()));
        }
    }

    @Nested
    @DisplayName("메시지 삭제 테스트")
    class DeleteMessageTest {

        @Test
        @DisplayName("존재하는 메시지 ID로 삭제를 요청하면 204 No Content를 반환한다")
        void shouldDeleteMessageSuccessfully_whenValidMessageIdProvided() throws Exception {
            // given: 메시지 삭제 서비스가 정상적으로 동작하도록 설정
            willDoNothing().given(messageService).delete(testMessageId);

            // when & then: DELETE 요청을 보내고 204 응답 검증
            mockMvc.perform(delete("/api/messages/" + testMessageId))
                .andDo(print())
                .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("존재하지 않는 메시지 ID로 삭제를 요청하면 MESSAGE_NOT_FOUND 상태코드로 응답한다")
        void shouldThrowException_whenMessageNotFoundForDeletion() throws Exception {
            // given: 존재하지 않는 메시지 ID 준비
            UUID nonExistentMessageId = UUID.randomUUID();
            willThrow(new com.sprint.mission.discodeit.exception.message.MessageNotFoundException("메시지를 찾을 수 없습니다.")).given(messageService).delete(nonExistentMessageId);

            // when & then: 존재하지 않는 메시지 ID로 DELETE 요청을 보내고 예외 응답 검증
            mockMvc.perform(delete("/api/messages/" + nonExistentMessageId))
                .andDo(print())
                .andExpect(status().is(com.sprint.mission.discodeit.exception.ErrorCode.MESSAGE_NOT_FOUND.getStatus().value()))
                .andExpect(jsonPath("$.code").value(com.sprint.mission.discodeit.exception.ErrorCode.MESSAGE_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.message").value(com.sprint.mission.discodeit.exception.ErrorCode.MESSAGE_NOT_FOUND.getMsg()));
        }
    }

    @Nested
    @DisplayName("메시지 조회 테스트")
    class FindMessageTest {

        @Test
        @DisplayName("존재하는 메시지 ID로 조회 요청 시 200 OK와 함께 메시지 정보를 반환한다")
        void shouldReturnMessage_whenValidMessageIdProvided() throws Exception {
            // given: 메시지 조회 서비스가 정상적으로 동작하도록 설정
            given(messageService.find(testMessageId)).willReturn(testMessageDto);

            // when & then: GET 요청을 보내고 응답 검증
            mockMvc.perform(get("/api/messages/" + testMessageId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testMessageId.toString()))
                .andExpect(jsonPath("$.channelId").value(testChannelId.toString()))
                .andExpect(jsonPath("$.content").value("테스트 메시지 내용"));
        }

        @Test
        @DisplayName("존재하지 않는 메시지 ID로 조회를 요청하면 MESSAGE_NOT_FOUND 상태코드로 응답한다")
        void shouldThrowException_whenMessageNotFoundForRetrieval() throws Exception {
            // given: 존재하지 않는 메시지 ID 준비
            UUID nonExistentMessageId = UUID.randomUUID();
            given(messageService.find(nonExistentMessageId))
                .willThrow(new com.sprint.mission.discodeit.exception.message.MessageNotFoundException("메시지를 찾을 수 없습니다."));

            // when & then: 존재하지 않는 메시지 ID로 GET 요청을 보내고 예외 응답 검증
            mockMvc.perform(get("/api/messages/" + nonExistentMessageId))
                .andDo(print())
                .andExpect(status().is(com.sprint.mission.discodeit.exception.ErrorCode.MESSAGE_NOT_FOUND.getStatus().value()))
                .andExpect(jsonPath("$.code").value(com.sprint.mission.discodeit.exception.ErrorCode.MESSAGE_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.message").value(com.sprint.mission.discodeit.exception.ErrorCode.MESSAGE_NOT_FOUND.getMsg()));
        }
    }

    @Nested
    @DisplayName("채널별 메시지 목록 조회 테스트")
    class FindMessagesByChannelIdTest {

        @Test
        @DisplayName("유효한 채널 ID로 메시지 목록 조회 요청 시 200 OK와 함께 페이지네이션된 메시지 목록을 반환한다")
        void shouldReturnMessagesByChannelId_whenValidChannelIdProvided() throws Exception {
            // given: 채널별 메시지 목록 데이터 준비
            MessageDto message2 = new MessageDto(
                UUID.randomUUID(),
                Instant.now(),
                Instant.now(),
                "두 번째 메시지",
                testChannelId,
                testAuthorDto,
                null
            );
            
            PageResponse<MessageDto> pageResponse = new PageResponse<>(
                Arrays.asList(testMessageDto, message2),
                Instant.now(),
                20,
                true,
                2L
            );
            
            given(messageService.findAllByChannelIdWithAuthor(eq(testChannelId), any(), any()))
                .willReturn(pageResponse);

            // when & then: GET 요청을 보내고 응답 검증
            mockMvc.perform(get("/api/messages")
                    .param("channelId", testChannelId.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.hasNext").value(true))
                .andExpect(jsonPath("$.content[0].content").value("테스트 메시지 내용"))
                .andExpect(jsonPath("$.content[1].content").value("두 번째 메시지"));
        }

        @Test
        @DisplayName("메시지가 없는 채널 ID로 조회 요청 시 200 OK와 함께 빈 페이지를 반환한다")
        void shouldReturnEmptyPage_whenNoMessagesExistForChannel() throws Exception {
            // given: 빈 메시지 목록 반환하도록 설정
            PageResponse<MessageDto> emptyPageResponse = new PageResponse<>(
                Arrays.asList(),
                null,
                20,
                false,
                0L
            );
            
            given(messageService.findAllByChannelIdWithAuthor(eq(testChannelId), any(), any()))
                .willReturn(emptyPageResponse);

            // when & then: GET 요청을 보내고 빈 페이지 응답 검증
            mockMvc.perform(get("/api/messages")
                    .param("channelId", testChannelId.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(0))
                .andExpect(jsonPath("$.hasNext").value(false));
        }
    }
} 