package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.config.TestJpaAuditingConfig;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.service.ChannelService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.AuditorAware;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(ChannelController.class)
@DisplayName("ChannelController 슬라이스 테스트")
public class ChannelControllerTest {

    @Autowired private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private ChannelService channelService;

    @Test
    @DisplayName("PUBLIC 채널 생성 API가 정상적으로 동작한다")
    void createPublicChannel_Success() throws Exception {
        // given
        PublicChannelCreateRequest publicChannelCreateRequest = new PublicChannelCreateRequest("test", "test채널");
        ChannelDto channelDto = new ChannelDto(UUID.randomUUID(), ChannelType.PUBLIC, "test", "test채널", null, null);

        given(channelService.create(publicChannelCreateRequest)).willReturn(channelDto);

        // when & then
        mockMvc.perform(post("/api/channels/public")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(publicChannelCreateRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.type").value(ChannelType.PUBLIC.name()))
                .andExpect(jsonPath("$.description").value("test채널"));
    }

    @Test
    @DisplayName("PRIVATE 채널 생성 API가 정상적으로 동작한다")
    void createPrivateChannel_Success() throws Exception {
        // given
        PrivateChannelCreateRequest privateChannelCreateRequest = new PrivateChannelCreateRequest(null);
        ChannelDto channelDto = new ChannelDto(UUID.randomUUID(), ChannelType.PRIVATE, "test", "test채널", null, null);

        given(channelService.create(privateChannelCreateRequest)).willReturn(channelDto);

        // when & then
        mockMvc.perform(post("/api/channels/private")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(privateChannelCreateRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.type").value(ChannelType.PRIVATE.name()))
                .andExpect(jsonPath("$.description").value("test채널"));
    }

    @Test
    @DisplayName("PUBLIC 채널 수정 API가 정상적으로 동작한다")
    void updatePublicChannel_Success() throws Exception {
        // given
        UUID channelId = UUID.randomUUID();
        PublicChannelUpdateRequest publicChannelUpdateRequest = new PublicChannelUpdateRequest("수정한 채널명", "수정한 설명");
        ChannelDto channelDto = new ChannelDto(channelId, ChannelType.PUBLIC, "수정한 채널명", "수정한 설명", null, null);

        given(channelService.update(channelId, publicChannelUpdateRequest)).willReturn(channelDto);
        // when & then
        mockMvc.perform(put("/api/channels/{channelId}", channelId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(publicChannelUpdateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("수정한 채널명"))
                .andExpect(jsonPath("$.type").value(ChannelType.PUBLIC.name()))
                .andExpect(jsonPath("$.description").value("수정한 설명"));
    }

    @Test
    @DisplayName("존재하지 않는 채널 수정 시 404 반환")
    void updateChannel_Fail_NotFound() throws Exception {
        // given
        UUID nonExistentId = UUID.randomUUID();
        PublicChannelUpdateRequest updateRequest = new PublicChannelUpdateRequest("채널", "설명");

        given(channelService.update(nonExistentId, updateRequest))
                .willThrow(new ChannelNotFoundException(nonExistentId));

        // when & then
        mockMvc.perform(put("/api/channels/{channelId}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

}
