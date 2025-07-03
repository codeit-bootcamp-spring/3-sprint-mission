package com.sprint.mission.discodeit.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.readstatus.DuplicateReadStatusException;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.service.ReadStatusService;
import com.sprint.mission.discodeit.testutil.TestDataBuilder;
import java.lang.reflect.Field;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ReadStatusController.class)
@DisplayName("ReadStatusController 슬라이스 테스트")
class ReadStatusControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private ReadStatusService readStatusService;

  private ReadStatus createReadStatusWithIds(UUID userId, UUID channelId, Instant lastReadAt) {
    try {
      User user = TestDataBuilder.createDefaultUser();
      Field userIdField = User.class.getSuperclass().getSuperclass().getDeclaredField("id");
      userIdField.setAccessible(true);
      userIdField.set(user, userId);

      Channel channel = TestDataBuilder.createPublicChannel();
      Field channelIdField = Channel.class.getSuperclass().getSuperclass().getDeclaredField("id");
      channelIdField.setAccessible(true);
      channelIdField.set(channel, channelId);

      return new ReadStatus(user, channel, lastReadAt);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  @DisplayName("읽기 상태 생성 - 성공")
  void create_Success() throws Exception {
    // given
    UUID userId = TestDataBuilder.USER_ID_1;
    UUID channelId = TestDataBuilder.CHANNEL_ID_1;
    Instant lastReadAt = Instant.now();
    ReadStatusCreateRequest request = new ReadStatusCreateRequest(userId, channelId, lastReadAt);
    ReadStatus readStatus = createReadStatusWithIds(userId, channelId, lastReadAt);

    when(readStatusService.create(request)).thenReturn(readStatus);

    // when & then
    mockMvc.perform(post("/api/readStatuses")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.userId").exists())
        .andExpect(jsonPath("$.channelId").exists())
        .andExpect(jsonPath("$.lastReadAt").exists());
  }

  @Test
  @DisplayName("읽기 상태 생성 - 실패 (유효하지 않은 요청 - 필수 필드 누락)")
  void create_Fail_InvalidRequest() throws Exception {
    // given
    ReadStatusCreateRequest invalidRequest = new ReadStatusCreateRequest(null, null, null);

    // when & then
    mockMvc.perform(post("/api/readStatuses")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("읽기 상태 생성 - 실패 (중복 읽기 상태)")
  void create_Fail_DuplicateReadStatus() throws Exception {
    // given
    UUID userId = TestDataBuilder.USER_ID_1;
    UUID channelId = TestDataBuilder.CHANNEL_ID_1;
    Instant lastReadAt = Instant.now();
    ReadStatusCreateRequest request = new ReadStatusCreateRequest(userId, channelId, lastReadAt);

    when(readStatusService.create(request)).thenThrow(DuplicateReadStatusException.of());

    // when & then
    mockMvc.perform(post("/api/readStatuses")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isConflict());
  }

  @Test
  @DisplayName("읽기 상태 수정 - 성공")
  void update_Success() throws Exception {
    // given
    UUID readStatusId = UUID.randomUUID();
    UUID userId = TestDataBuilder.USER_ID_1;
    UUID channelId = TestDataBuilder.CHANNEL_ID_1;
    Instant newLastReadAt = Instant.now();
    ReadStatusUpdateRequest request = new ReadStatusUpdateRequest(newLastReadAt);
    ReadStatus updatedReadStatus = createReadStatusWithIds(userId, channelId, newLastReadAt);

    when(readStatusService.update(readStatusId, request)).thenReturn(updatedReadStatus);

    // when & then
    mockMvc.perform(patch("/api/readStatuses/{readStatusId}", readStatusId)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userId").exists())
        .andExpect(jsonPath("$.channelId").exists())
        .andExpect(jsonPath("$.lastReadAt").exists());
  }

  @Test
  @DisplayName("읽기 상태 수정 - 실패 (존재하지 않는 읽기 상태)")
  void update_Fail_ReadStatusNotFound() throws Exception {
    // given
    UUID readStatusId = UUID.randomUUID();
    Instant newLastReadAt = Instant.now();
    ReadStatusUpdateRequest request = new ReadStatusUpdateRequest(newLastReadAt);

    when(readStatusService.update(readStatusId, request))
        .thenThrow(ReadStatusNotFoundException.withReadStatusId(readStatusId));

    // when & then
    mockMvc.perform(patch("/api/readStatuses/{readStatusId}", readStatusId)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("읽기 상태 수정 - 실패 (유효하지 않은 요청)")
  void update_Fail_InvalidRequest() throws Exception {
    // given
    UUID readStatusId = UUID.randomUUID();
    ReadStatusUpdateRequest invalidRequest = new ReadStatusUpdateRequest(null);

    // when & then
    mockMvc.perform(patch("/api/readStatuses/{readStatusId}", readStatusId)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("사용자별 읽기 상태 조회 - 성공")
  void findAllByUserId_Success() throws Exception {
    // given
    UUID userId = TestDataBuilder.USER_ID_1;
    UUID channelId1 = TestDataBuilder.CHANNEL_ID_1;
    UUID channelId2 = TestDataBuilder.CHANNEL_ID_2;
    Instant now = Instant.now();
    List<ReadStatus> readStatuses = List.of(
        createReadStatusWithIds(userId, channelId1, now),
        createReadStatusWithIds(userId, channelId2, now));

    when(readStatusService.findAllByUserId(userId)).thenReturn(readStatuses);

    // when & then
    mockMvc.perform(get("/api/readStatuses")
        .param("userId", userId.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(readStatuses.size()))
        .andExpect(jsonPath("$[0].userId").exists())
        .andExpect(jsonPath("$[0].channelId").exists())
        .andExpect(jsonPath("$[1].userId").exists())
        .andExpect(jsonPath("$[1].channelId").exists());
  }

  @Test
  @DisplayName("사용자별 읽기 상태 조회 - 성공 (빈 목록)")
  void findAllByUserId_Success_EmptyList() throws Exception {
    // given
    UUID userId = UUID.randomUUID();
    List<ReadStatus> emptyList = List.of();

    when(readStatusService.findAllByUserId(userId)).thenReturn(emptyList);

    // when & then
    mockMvc.perform(get("/api/readStatuses")
        .param("userId", userId.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(0));
  }

  @Test
  @DisplayName("사용자별 읽기 상태 조회 - 실패 (userId 파라미터 누락)")
  void findAllByUserId_Fail_MissingUserId() throws Exception {
    // when & then
    mockMvc.perform(get("/api/readStatuses"))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("사용자별 읽기 상태 조회 - 실패 (유효하지 않은 UUID 형식)")
  void findAllByUserId_Fail_InvalidUuidFormat() throws Exception {
    // when & then
    mockMvc.perform(get("/api/readStatuses")
        .param("userId", "invalid-uuid"))
        .andExpect(status().isBadRequest());
  }
}
