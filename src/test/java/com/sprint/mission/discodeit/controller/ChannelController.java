package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.service.ChannelService;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@WebMvcTest(controllers = ChannelController.class)
@DisplayName("ChannelController 슬라이스 테스트")
public class ChannelController {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private ChannelService channelService;

    @Test
    @DisplayName("공개 채널 생성 API")
    void createPublicChannel() throws Exception {
        // given
        PublicChannelCreateRequest req = new PublicChannelCreateRequest("public", "public channel");

        ChannelDto respDto = new ChannelDto(
            UUID.randomUUID(), ChannelType.PUBLIC, "public", "public channel", null, Instant.now()
        );
        given(channelService.create(any(PublicChannelCreateRequest.class))).willReturn(respDto);

        // when, then
        mockMvc.perform(post("/api/channels/public")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))
            )
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(respDto.id().toString()))
            .andExpect(jsonPath("$.name").value("공용채널"))
            .andExpect(jsonPath("$.isPrivate").value(false));

        then(channelService).should(times(1))
            .create(any(PublicChannelCreateRequest.class));
    }

    @Test
    @DisplayName("비공개 채널 생성 API")
    void createPrivateChannel() throws Exception {
        // given
        PrivateChannelCreateRequest req = new PrivateChannelCreateRequest(null);
        ChannelDto channelDto = new ChannelDto(
            UUID.randomUUID(),
            ChannelType.PRIVATE,
            "private",
            "private channel",
            null,
            Instant.now()
        );
        given(channelService.create(any(PrivateChannelCreateRequest.class))).willReturn(channelDto);

        // when, then
        mockMvc.perform(post("/api/channels/private")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))
            )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(channelDto.id().toString()))
            .andExpect(jsonPath("$.name").value("private"))
            .andExpect(jsonPath("$.isPrivate").value(true));

        then(channelService).should(times(1)).create(any(PrivateChannelCreateRequest.class));
    }

    @Test
    @DisplayName("채널 정보 수정 API")
    void updateChannel() throws Exception {
        // given
        UUID channelId = UUID.randomUUID();
        PublicChannelUpdateRequest req = new PublicChannelUpdateRequest("publicChannel", "public Channel.");

        ChannelDto channelDto = new ChannelDto(channelId, ChannelType.PUBLIC, "publicChannel", "public Channel.", null, Instant.MIN);
        given(channelService.update(channelId, any(PublicChannelUpdateRequest.class))).willReturn(channelDto);

        // when, then
        mockMvc.perform(patch("/api/channels/{channelId}", channelId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(channelId.toString()))
            .andExpect(jsonPath("$.name").value("publicChannel"))
            .andExpect(jsonPath("$.description").value("public Channel."));

        then(channelService).should(times(1))
            .update(channelId, any(PublicChannelUpdateRequest.class));
    }

    @Test
    @DisplayName("채널 삭제 API")
    void deleteChannel() throws Exception {
        // given
        UUID channelId = UUID.randomUUID();
        willDoNothing().given(channelService).delete(channelId);

        // when, then
        mockMvc.perform(delete("/api/channels/{channelId}", channelId)).andExpect(status().isNoContent());

        then(channelService).should(times(1)).delete(channelId);
    }
}