package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelResponse;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.service.ChannelService;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ChannelController.class)
@Import(GlobalExceptionHandler.class)
class ChannelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ChannelService channelService;

    @MockitoBean(name = "jpaMappingContext")
    Object dummyJpaMappingContext;

    @MockitoBean(name = "jpaAuditingHandler")
    Object dummyJpaAuditingHandler;

    @Test
    @DisplayName("공개 채널 생성 성공")
    @WithMockUser(username = "manager", roles = {"CHANNEL_MANAGER"})
    void shouldCreatePublicChannelSuccessfully() throws Exception {
        PublicChannelCreateRequest request = new PublicChannelCreateRequest("general", "설명입니다.");
        ChannelResponse response = new ChannelResponse(UUID.randomUUID(), ChannelType.PUBLIC,
            "general", "설명입니다.", List.of(), Instant.now());

        BDDMockito.given(channelService.createPublicChannel(any())).willReturn(response);

        mockMvc.perform(
                post("/api/channels/public").with(csrf()).contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))).andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("general"))
            .andExpect(jsonPath("$.type").value("PUBLIC"))
            .andExpect(jsonPath("$.description").value("설명입니다."));
    }

    @Test
    @DisplayName("채널 업데이트 성공")
    @WithMockUser(username = "manager", roles = {"CHANNEL_MANAGER"})
    void shouldUpdateChannelSuccessfully() throws Exception {
        UUID channelId = UUID.randomUUID();
        PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("updated-channel",
            "업데이트 설명");
        ChannelResponse response = new ChannelResponse(channelId, ChannelType.PUBLIC,
            "updated-channel", "업데이트 설명", List.of(), Instant.now());

        BDDMockito.given(channelService.update(channelId, request)).willReturn(response);

        mockMvc.perform(patch("/api/channels/{channelId}", channelId).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))).andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("updated-channel"))
            .andExpect(jsonPath("$.description").value("업데이트 설명"));
    }

    @Test
    @DisplayName("채널 삭제 성공")
    @WithMockUser(username = "manager", roles = {"CHANNEL_MANAGER"})
    void shouldDeleteChannelSuccessfully() throws Exception {
        UUID channelId = UUID.randomUUID();
        ChannelResponse response = new ChannelResponse(channelId, ChannelType.PUBLIC, "general",
            "설명입니다.", List.of(), Instant.now());

        BDDMockito.given(channelService.delete(channelId)).willReturn(response);

        mockMvc.perform(delete("/api/channels/{channelId}", channelId).with(csrf()))

            .andExpect(status().isOk()).andExpect(jsonPath("$.name").value("general"))
            .andExpect(jsonPath("$.description").value("설명입니다."));
    }

}
