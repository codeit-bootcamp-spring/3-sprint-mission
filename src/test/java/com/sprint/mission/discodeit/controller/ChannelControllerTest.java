package com.sprint.mission.discodeit.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.mapper.mapstruct.MapperFacade;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.DuplicateParticipantsException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.testutil.TestDataBuilder;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ChannelController.class)
@DisplayName("ChannelController 슬라이스 테스트")
class ChannelControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private ChannelService channelService;

  @MockitoBean
  private MapperFacade mapperFacade;

  private Channel createChannelWithId(UUID channelId, ChannelType type, String name, String description) {
    try {
      Channel channel = new Channel(type, name, description);
      Field idField = Channel.class.getSuperclass().getSuperclass().getDeclaredField("id");
      idField.setAccessible(true);
      idField.set(channel, channelId);
      return channel;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  @DisplayName("공개 채널 생성 - 성공")
  void createPublicChannel_Success() throws Exception {
    // given
    PublicChannelCreateRequest request = TestDataBuilder.createPublicChannelCreateRequest();
    Channel createdChannel = createChannelWithId(TestDataBuilder.CHANNEL_ID_1, ChannelType.PUBLIC,
        request.name(), request.description());
    ChannelDto channelDto = TestDataBuilder.createPublicChannelDto();

    when(channelService.create(request)).thenReturn(createdChannel);
    when(mapperFacade.toDto(createdChannel)).thenReturn(channelDto);

    // when & then
    mockMvc.perform(post("/api/channels/public")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(channelDto.id().toString()))
        .andExpect(jsonPath("$.type").value(channelDto.type().toString()))
        .andExpect(jsonPath("$.name").value(channelDto.name()))
        .andExpect(jsonPath("$.description").value(channelDto.description()));
  }

  @Test
  @DisplayName("공개 채널 생성 - 실패 (유효하지 않은 요청)")
  void createPublicChannel_Fail_InvalidRequest() throws Exception {
    // given
    PublicChannelCreateRequest invalidRequest = new PublicChannelCreateRequest("", "");

    // when & then
    mockMvc.perform(post("/api/channels/public")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("비공개 채널 생성 - 성공")
  void createPrivateChannel_Success() throws Exception {
    // given
    PrivateChannelCreateRequest request = TestDataBuilder.createPrivateChannelCreateRequest();
    Channel createdChannel = createChannelWithId(TestDataBuilder.CHANNEL_ID_2, ChannelType.PRIVATE, null, null);
    ChannelDto channelDto = TestDataBuilder.createPrivateChannelDto();

    when(channelService.create(request)).thenReturn(createdChannel);
    when(mapperFacade.toDto(createdChannel)).thenReturn(channelDto);

    // when & then
    mockMvc.perform(post("/api/channels/private")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(channelDto.id().toString()))
        .andExpect(jsonPath("$.type").value(channelDto.type().toString()))
        .andExpect(jsonPath("$.name").doesNotExist())
        .andExpect(jsonPath("$.description").doesNotExist())
        .andExpect(jsonPath("$.participants").isArray());
  }

  @Test
  @DisplayName("비공개 채널 생성 - 실패 (중복된 참가자)")
  void createPrivateChannel_Fail_DuplicateParticipants() throws Exception {
    // given
    UUID duplicateUserId = TestDataBuilder.USER_ID_1;
    PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(
        Arrays.asList(duplicateUserId, duplicateUserId) // 중복된 사용자 ID
    );

    when(channelService.create(request)).thenThrow(DuplicateParticipantsException.of());

    // when & then
    mockMvc.perform(post("/api/channels/private")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("비공개 채널 생성 - 실패 (유효하지 않은 요청)")
  void createPrivateChannel_Fail_InvalidRequest() throws Exception {
    // given
    PrivateChannelCreateRequest invalidRequest = new PrivateChannelCreateRequest(null);

    // when & then
    mockMvc.perform(post("/api/channels/private")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("채널 수정 - 성공")
  void updateChannel_Success() throws Exception {
    // given
    UUID channelId = TestDataBuilder.CHANNEL_ID_1;
    PublicChannelUpdateRequest request = TestDataBuilder.createPublicChannelUpdateRequest();
    Channel updatedChannel = createChannelWithId(channelId, ChannelType.PUBLIC,
        request.newName(), request.newDescription());

    when(channelService.update(channelId, request)).thenReturn(updatedChannel);

    // when & then
    mockMvc.perform(patch("/api/channels/{channelId}", channelId)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(channelId.toString()))
        .andExpect(jsonPath("$.name").value(request.newName()))
        .andExpect(jsonPath("$.description").value(request.newDescription()));
  }

  @Test
  @DisplayName("채널 수정 - 실패 (존재하지 않는 채널)")
  void updateChannel_Fail_ChannelNotFound() throws Exception {
    // given
    UUID channelId = UUID.randomUUID();
    PublicChannelUpdateRequest request = TestDataBuilder.createPublicChannelUpdateRequest();

    when(channelService.update(channelId, request)).thenThrow(ChannelNotFoundException.withChannelId(channelId));

    // when & then
    mockMvc.perform(patch("/api/channels/{channelId}", channelId)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("채널 수정 - 실패 (비공개 채널 수정 시도)")
  void updateChannel_Fail_PrivateChannelUpdate() throws Exception {
    // given
    UUID channelId = TestDataBuilder.CHANNEL_ID_2;
    PublicChannelUpdateRequest request = TestDataBuilder.createPublicChannelUpdateRequest();

    when(channelService.update(channelId, request)).thenThrow(PrivateChannelUpdateException.of());

    // when & then
    mockMvc.perform(patch("/api/channels/{channelId}", channelId)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("채널 수정 - 실패 (유효하지 않은 요청)")
  void updateChannel_Fail_InvalidRequest() throws Exception {
    // given
    UUID channelId = TestDataBuilder.CHANNEL_ID_1;
    // @Size 제약조건을 위반하는 요청 (100자 초과)
    String longName = "a".repeat(101); // CHANNEL_NAME_MAX_LENGTH = 100을 초과
    PublicChannelUpdateRequest invalidRequest = new PublicChannelUpdateRequest(longName, "");

    // when & then
    mockMvc.perform(patch("/api/channels/{channelId}", channelId)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("채널 삭제 - 성공")
  void deleteChannel_Success() throws Exception {
    // given
    UUID channelId = TestDataBuilder.CHANNEL_ID_1;
    doNothing().when(channelService).delete(channelId);

    // when & then
    mockMvc.perform(delete("/api/channels/{channelId}", channelId))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("채널 삭제 - 실패 (존재하지 않는 채널)")
  void deleteChannel_Fail_ChannelNotFound() throws Exception {
    // given
    UUID channelId = UUID.randomUUID();
    doThrow(ChannelNotFoundException.withChannelId(channelId)).when(channelService).delete(channelId);

    // when & then
    mockMvc.perform(delete("/api/channels/{channelId}", channelId))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("사용자별 채널 목록 조회 - 성공")
  void findAllByUserId_Success() throws Exception {
    // given
    UUID userId = TestDataBuilder.USER_ID_1;
    List<ChannelDto> channels = Arrays.asList(
        TestDataBuilder.createPublicChannelDto(),
        TestDataBuilder.createPrivateChannelDto());

    when(channelService.findAllByUserId(userId)).thenReturn(channels);

    // when & then
    mockMvc.perform(get("/api/channels")
        .param("userId", userId.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(channels.size()))
        .andExpect(jsonPath("$[0].id").value(channels.get(0).id().toString()))
        .andExpect(jsonPath("$[0].type").value(channels.get(0).type().toString()))
        .andExpect(jsonPath("$[1].id").value(channels.get(1).id().toString()))
        .andExpect(jsonPath("$[1].type").value(channels.get(1).type().toString()));
  }

  @Test
  @DisplayName("사용자별 채널 목록 조회 - 성공 (빈 목록)")
  void findAllByUserId_Success_EmptyList() throws Exception {
    // given
    UUID userId = UUID.randomUUID();
    List<ChannelDto> emptyList = List.of();

    when(channelService.findAllByUserId(userId)).thenReturn(emptyList);

    // when & then
    mockMvc.perform(get("/api/channels")
        .param("userId", userId.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(0));
  }

  @Test
  @DisplayName("사용자별 채널 목록 조회 - 실패 (userId 파라미터 누락)")
  void findAllByUserId_Fail_MissingUserId() throws Exception {
    // when & then
    mockMvc.perform(get("/api/channels"))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("사용자별 채널 목록 조회 - 실패 (유효하지 않은 UUID 형식)")
  void findAllByUserId_Fail_InvalidUuidFormat() throws Exception {
    // when & then
    mockMvc.perform(get("/api/channels")
        .param("userId", "invalid-uuid"))
        .andExpect(status().isBadRequest());
  }
}
