package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.service.ChannelService;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ChannelController.class)
class ChannelControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ChannelService channelService;

    private UUID channelId;
    private UUID userId;
    private ChannelDto publicChannel;
    private ChannelDto privateChannel;
    private PublicChannelUpdateRequest updateChannelRequest;

    @BeforeEach
    void setUp() {
        channelId = UUID.randomUUID();
        userId = UUID.randomUUID();

        publicChannel = new ChannelDto(channelId, ChannelType.PUBLIC, "공개채널", "공개채널입니다.",
            new ArrayList<>(),
            Instant.now());

        privateChannel = new ChannelDto(
            UUID.randomUUID(),
            ChannelType.PRIVATE,
            null,
            null,
            List.of(new UserDto(userId, "user1", "user1@abc.com", null, true)),
            Instant.now()
        );

        updateChannelRequest = new PublicChannelUpdateRequest("새이름", "새설명");

    }

    @Test
    @DisplayName("공개 채널 생성 성공")
    void createPublicChannel_Success() throws Exception {
        PublicChannelCreateRequest request = new PublicChannelCreateRequest("공개채널", "공개채널입니다.");

        given(channelService.create(any(PublicChannelCreateRequest.class))).willReturn(
            publicChannel);

        mockMvc.perform(post("/api/channels/public")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(publicChannel.id().toString()))
            .andExpect(jsonPath("$.type").value("PUBLIC"))
            .andExpect(jsonPath("$.name").value("공개채널"))
            .andExpect(jsonPath("$.description").value("공개채널입니다."));
    }

    @Test
    @DisplayName("공개 채널 생성 실패")
    void createPublicChannel_Failure() throws Exception {
        PublicChannelCreateRequest invalidRequest = new PublicChannelCreateRequest("Q", "공개채널입니다.");

        mockMvc.perform(post("/api/channels/public")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("비공개 채널 생성 성공")
    void createPrivateChannel_Success() throws Exception {
        List<UUID> participantIds = List.of(userId, UUID.randomUUID());
        PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(participantIds);

        given(channelService.create(any(PrivateChannelCreateRequest.class))).willReturn(
            privateChannel);

        mockMvc.perform(post("/api/channels/private")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(privateChannel.id().toString()))
            .andExpect(jsonPath("$.type").value("PRIVATE"))
            .andExpect(jsonPath("$.participants[0].id").value(userId.toString()));
    }

    @Test
    @DisplayName("공개채널 수정 성공")
    void updateChannel_Success() throws Exception {
        ChannelDto updatedChannel = new ChannelDto(
            channelId,
            ChannelType.PUBLIC,
            "new 공개채널",
            "new 공개채널 입니다.",
            List.of(),
            Instant.now()
        );

        given(
            channelService.update(eq(channelId), any(PublicChannelUpdateRequest.class))).willReturn(
            updatedChannel);

        mockMvc.perform(patch("/api/channels/{channelId}", channelId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateChannelRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(channelId.toString()))
            .andExpect(jsonPath("$.name").value("new 공개채널"));
    }

    @Test
    @DisplayName("비공개채널 수정 실패")
    void updateChannel_Failure() throws Exception {
        UUID privateChannelId = UUID.randomUUID();

        given(channelService.update(eq(privateChannelId), any(PublicChannelUpdateRequest.class)))
            .willThrow(PrivateChannelUpdateException.withChannel(privateChannelId));

        mockMvc.perform(patch("/api/channels/{channelId}", privateChannelId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateChannelRequest)))
            .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("채널 삭제 성공")
    void deleteChannel_Success() throws Exception {
        willDoNothing().given(channelService).deleteChannel(channelId);

        mockMvc.perform(delete("/api/channels/{channelId}", channelId))
            .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("채널 삭제 실패")
    void deleteChannel_Failure() throws Exception {
        UUID nonExistentChannelId = UUID.randomUUID();

        willThrow(ChannelNotFoundException.withId(nonExistentChannelId))
            .given(channelService).deleteChannel(nonExistentChannelId);

        mockMvc.perform(delete("/api/channels/{channelId}", nonExistentChannelId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("사용자의 채널 목록 조회 성공")
    void findAllChannels_Success() throws Exception {
        given(channelService.findAllByUserId(userId)).willReturn(
            List.of(publicChannel, privateChannel));

        mockMvc.perform(get("/api/channels")
                .param("userId", userId.toString())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(publicChannel.id().toString()))
            .andExpect(jsonPath("$[0].type").value("PUBLIC"))
            .andExpect(jsonPath("$[1].type").value("PRIVATE"));
    }

}