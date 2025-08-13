package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.testutil.TestDataBuilder;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MessageController.class)
@DisplayName("MessageController 슬라이스 테스트")
class MessageControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private MessageService messageService;

  @Test
  @DisplayName("메시지 생성 - 성공 (첨부파일 없음)")
  void create_Success_WithoutAttachments() throws Exception {
    // given
    MessageCreateRequest request = TestDataBuilder.createMessageCreateRequest();
    MessageDto responseDto = TestDataBuilder.createMessageDto();

    when(messageService.create(eq(request), eq(List.of()))).thenReturn(responseDto);

    MockMultipartFile requestPart = new MockMultipartFile(
        "messageCreateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(request));

    // when & then
    mockMvc.perform(multipart("/api/messages")
        .file(requestPart)
        .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(responseDto.id().toString()))
        .andExpect(jsonPath("$.content").value(responseDto.content()))
        .andExpect(jsonPath("$.channelId").value(responseDto.channelId().toString()))
        .andExpect(jsonPath("$.author.id").value(responseDto.author().id().toString()));
  }

  @Test
  @DisplayName("메시지 생성 - 성공 (첨부파일 포함)")
  void create_Success_WithAttachments() throws Exception {
    // given
    MessageCreateRequest request = TestDataBuilder.createMessageCreateRequest();
    MessageDto responseDto = TestDataBuilder.createMessageDto();

    when(messageService.create(eq(request), anyList())).thenReturn(responseDto);

    MockMultipartFile requestPart = new MockMultipartFile(
        "messageCreateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(request));

    MockMultipartFile attachment1 = new MockMultipartFile(
        "attachments",
        "file1.txt",
        MediaType.TEXT_PLAIN_VALUE,
        "file content 1".getBytes());

    MockMultipartFile attachment2 = new MockMultipartFile(
        "attachments",
        "file2.jpg",
        MediaType.IMAGE_JPEG_VALUE,
        "file content 2".getBytes());

    // when & then
    mockMvc.perform(multipart("/api/messages")
        .file(requestPart)
        .file(attachment1)
        .file(attachment2)
        .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(responseDto.id().toString()))
        .andExpect(jsonPath("$.content").value(responseDto.content()));
  }

  @Test
  @DisplayName("메시지 생성 - 실패 (유효하지 않은 요청)")
  void create_Fail_InvalidRequest() throws Exception {
    // given
    MessageCreateRequest invalidRequest = new MessageCreateRequest("", null, null);

    MockMultipartFile requestPart = new MockMultipartFile(
        "messageCreateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(invalidRequest));

    // when & then
    mockMvc.perform(multipart("/api/messages")
        .file(requestPart)
        .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("메시지 수정 - 성공")
  void update_Success() throws Exception {
    // given
    UUID messageId = TestDataBuilder.MESSAGE_ID_1;
    MessageUpdateRequest request = TestDataBuilder.createMessageUpdateRequest();
    MessageDto responseDto = TestDataBuilder.createUpdatedMessageDto();

    when(messageService.update(messageId, request)).thenReturn(responseDto);

    // when & then
    mockMvc.perform(patch("/api/messages/{messageId}", messageId)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(responseDto.id().toString()))
        .andExpect(jsonPath("$.content").value(responseDto.content()));
  }

  @Test
  @DisplayName("메시지 수정 - 실패 (존재하지 않는 메시지)")
  void update_Fail_MessageNotFound() throws Exception {
    // given
    UUID messageId = UUID.randomUUID();
    MessageUpdateRequest request = TestDataBuilder.createMessageUpdateRequest();

    when(messageService.update(messageId, request))
        .thenThrow(MessageNotFoundException.withMessageId(messageId));

    // when & then
    mockMvc.perform(patch("/api/messages/{messageId}", messageId)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("메시지 수정 - 실패 (유효하지 않은 요청)")
  void update_Fail_InvalidRequest() throws Exception {
    // given
    UUID messageId = TestDataBuilder.MESSAGE_ID_1;
    MessageUpdateRequest invalidRequest = new MessageUpdateRequest(""); // 빈 문자열

    // when & then
    mockMvc.perform(patch("/api/messages/{messageId}", messageId)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("메시지 삭제 - 성공")
  void delete_Success() throws Exception {
    // given
    UUID messageId = TestDataBuilder.MESSAGE_ID_1;
    doNothing().when(messageService).delete(messageId);

    // when & then
    mockMvc.perform(delete("/api/messages/{messageId}", messageId))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("메시지 삭제 - 실패 (존재하지 않는 메시지)")
  void delete_Fail_MessageNotFound() throws Exception {
    // given
    UUID messageId = UUID.randomUUID();
    doThrow(MessageNotFoundException.withMessageId(messageId))
        .when(messageService).delete(messageId);

    // when & then
    mockMvc.perform(delete("/api/messages/{messageId}", messageId))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("채널별 메시지 목록 조회 - 성공")
  void findAllByChannelId_Success() throws Exception {
    // given
    UUID channelId = TestDataBuilder.CHANNEL_ID_1;
    List<MessageDto> messages = TestDataBuilder.createMessageDtoList();
    PageResponse<MessageDto> pageResponse = new PageResponse<>(
        messages, null, 20, false, null);

    when(messageService.findAllByChannelIdWithCursorPaging(
        eq(channelId), isNull(), any(Pageable.class)))
        .thenReturn(pageResponse);

    // when & then
    mockMvc.perform(get("/api/messages")
        .param("channelId", channelId.toString())
        .param("size", "20")
        .param("sort", "createdAt,desc"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content.length()").value(messages.size()))
        .andExpect(jsonPath("$.hasNext").value(false));
  }

  @Test
  @DisplayName("채널별 메시지 목록 조회 - 성공 (커서 페이징)")
  void findAllByChannelId_Success_WithCursor() throws Exception {
    // given
    UUID channelId = TestDataBuilder.CHANNEL_ID_1;
    String cursor = "2024-01-01T00:00:00Z";
    List<MessageDto> messages = TestDataBuilder.createMessageDtoList();
    PageResponse<MessageDto> pageResponse = new PageResponse<>(
        messages, "2024-01-01T01:00:00Z", 20, true, null);

    when(messageService.findAllByChannelIdWithCursorPaging(
        eq(channelId), eq(cursor), any(Pageable.class)))
        .thenReturn(pageResponse);

    // when & then
    mockMvc.perform(get("/api/messages")
        .param("channelId", channelId.toString())
        .param("cursor", cursor)
        .param("size", "20"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.hasNext").value(true))
        .andExpect(jsonPath("$.nextCursor").value("2024-01-01T01:00:00Z"));
  }

  @Test
  @DisplayName("채널별 메시지 목록 조회 - 성공 (빈 목록)")
  void findAllByChannelId_Success_EmptyList() throws Exception {
    // given
    UUID channelId = TestDataBuilder.CHANNEL_ID_1;
    PageResponse<MessageDto> emptyPageResponse = new PageResponse<>(
        List.of(), null, 20, false, null);

    when(messageService.findAllByChannelIdWithCursorPaging(
        eq(channelId), isNull(), any(Pageable.class)))
        .thenReturn(emptyPageResponse);

    // when & then
    mockMvc.perform(get("/api/messages")
        .param("channelId", channelId.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content.length()").value(0))
        .andExpect(jsonPath("$.hasNext").value(false));
  }

  @Test
  @DisplayName("채널별 메시지 목록 조회 - 실패 (channelId 파라미터 누락)")
  void findAllByChannelId_Fail_MissingChannelId() throws Exception {
    // when & then
    mockMvc.perform(get("/api/messages"))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("채널별 메시지 목록 조회 - 실패 (유효하지 않은 UUID 형식)")
  void findAllByChannelId_Fail_InvalidUuidFormat() throws Exception {
    // when & then
    mockMvc.perform(get("/api/messages")
        .param("channelId", "invalid-uuid"))
        .andExpect(status().isBadRequest());
  }
}
