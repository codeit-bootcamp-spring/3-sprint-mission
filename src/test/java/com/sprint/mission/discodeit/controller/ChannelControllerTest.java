package com.sprint.mission.discodeit.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ChannelController.class)
@ActiveProfiles("test")
@DisplayName("ChannelController 슬라이스 테스트")
@Import({GlobalExceptionHandler.class})
class ChannelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ChannelService channelService;

    @Test
    @DisplayName("공개 채널 생성 - 성공")
    void createPublic_Success() throws Exception {
        // Given
        PublicChannelCreateRequest request = new PublicChannelCreateRequest("채널1", "테스트 채널입니다.");
        ChannelDto channelDto = new ChannelDto(UUID.randomUUID(), ChannelType.PUBLIC,
            request.name(), request.description(), null, null);

        String requestJson = objectMapper.writeValueAsString(request);
        given(channelService.create(request)).willReturn(channelDto);

        // When & Then
        mockMvc.perform(post("/api/channels/public")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.type").value(ChannelType.PUBLIC.name()))
            .andExpect(jsonPath("$.name").value("채널1"))
            .andExpect(jsonPath("$.description").value("테스트 채널입니다."))
            .andDo(print());
    }

    @Test
    @DisplayName("비공개 채널 생성 - 성공")
    void createPrivate_Success() throws Exception {
        // Given
        List<UUID> participantIds = List.of(UUID.randomUUID(), UUID.randomUUID());
        PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(participantIds);
        ChannelDto channelDto = new ChannelDto(UUID.randomUUID(), ChannelType.PRIVATE, null, null,
            null, null);

        String requestJson = objectMapper.writeValueAsString(request);
        given(channelService.create(request)).willReturn(channelDto);

        // When & Then
        mockMvc.perform(post("/api/channels/private")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.type").value(ChannelType.PRIVATE.name()))
            .andDo(print());
    }

    @Test
    @DisplayName("비공개 채널 생성 - 실패 (참여자 없음)")
    void createPrivate_Fail() throws Exception {
        // Given
        PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(null);
        ChannelDto channelDto = new ChannelDto(UUID.randomUUID(), ChannelType.PRIVATE, null, null,
            null, null);

        String requestJson = objectMapper.writeValueAsString(request);

        // When & Then
        mockMvc.perform(post("/api/channels/private")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
            .andDo(print());
    }

    @Test
    @DisplayName("채널 전체 조회 - 성공")
    void findAll_Success() throws Exception {
        // Given
        UUID userId = UUID.randomUUID();

        // When & Then
        mockMvc.perform(get("/api/channels")
                .param("userId", userId.toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(0))
            .andDo(print());
    }

    @Test
    @DisplayName("공개 채널 수정 - 성공")
    void update_Success() throws Exception {
        // Given
        UUID channelId = UUID.randomUUID();
        PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("새채널", "새채널입니다.");
        ChannelDto channelDto = new ChannelDto(UUID.randomUUID(), ChannelType.PUBLIC,
            request.newName(), request.newDescription(), null, null);

        String requestJson = objectMapper.writeValueAsString(request);
        given(channelService.update(channelId, request)).willReturn(channelDto);

        // When & Then
        mockMvc.perform(patch("/api/channels/{channelId}", channelId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.type").value(ChannelType.PUBLIC.name()))
            .andExpect(jsonPath("$.name").value("새채널"))
            .andExpect(jsonPath("$.description").value("새채널입니다."))
            .andDo(print());
    }

    @Test
    @DisplayName("공개 채널 수정 - 실패 (private 채널)")
    void update_Fail() throws Exception {
        // Given
        UUID channelId = UUID.randomUUID();
        PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("새채널", "설명");
        String requestJson = objectMapper.writeValueAsString(request);

        given(channelService.update(channelId, request))
            .willThrow(new PrivateChannelUpdateException(channelId));

        // When & Then
        mockMvc.perform(patch("/api/channels/{channelId}", channelId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.code").value("PRIVATE_CHANNEL_UPDATE"))
            .andDo(print());

    }

    @Test
    @DisplayName("채널 삭제 - 성공")
    void delete_Success() throws Exception {
        // Given
        UUID channelId = UUID.randomUUID();

        // When & Then
        mockMvc.perform(delete("/api/channels/{channelId}", channelId))
            .andExpect(status().isNoContent())
            .andDo(print());
    }
}