package com.sprint.mission.discodeit.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.service.ChannelService;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ChannelController.class)
@ActiveProfiles("test")
@DisplayName("ChannelController 테스트")
class ChannelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ChannelService channelService;

    @Test
    @DisplayName("공개 채널 생성 - 성공")
    void createPublicChannel_Success() throws Exception {
        // given
        PublicChannelCreateRequest request = new PublicChannelCreateRequest(
            "Test Channel",
            "Test channel description"
        );

        ChannelDto responseDto = new ChannelDto(
            UUID.randomUUID(),
            ChannelType.PUBLIC,
            "Test Channel",
            "Test channel description",
            Arrays.asList(),
            null
        );

        given(channelService.create(request)).willReturn(responseDto);

        // when & then
        mockMvc.perform(post("/api/channels/public")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("Test Channel"))
            .andExpect(jsonPath("$.description").value("Test channel description"))
            .andExpect(jsonPath("$.type").value("PUBLIC"));
    }

    @Test
    @DisplayName("공개 채널 생성 - 실패 (유효성 검증 오류)")
    void createPublicChannel_ValidationFailure() throws Exception {
        // given
        PublicChannelCreateRequest invalidRequest = new PublicChannelCreateRequest(
            "asdqweaxczxcfsfrkljsertfklshvfujsgefrjykhawgfjhszgchjzsfdvshfvxcchgszgfejhzsfjhkxgvyxdujkfgbzsjhfgbszjhkfgzxjhvg",
            "Test description"
        );

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
        PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(
            Arrays.asList(UUID.randomUUID(), UUID.randomUUID()));

        List<UserDto> participants = Arrays.asList(
            new UserDto(UUID.randomUUID(), "user1", "user1@example.com", null, true),
            new UserDto(UUID.randomUUID(), "user2", "user2@example.com", null, false)
        );

        ChannelDto responseDto = new ChannelDto(
            UUID.randomUUID(),
            ChannelType.PRIVATE,
            null,
            null,
            participants,
            null
        );

        given(channelService.create(request)).willReturn(responseDto);

        // when & then
        mockMvc.perform(post("/api/channels/private")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.type").value("PRIVATE"))
            .andExpect(jsonPath("$.participants").isArray())
            .andExpect(jsonPath("$.participants.length()").value(2));
    }

    @Test
    @DisplayName("비공개 채널 생성 - 실패 (빈 참가자 목록)")
    void createPrivateChannel_ValidationFailure() throws Exception {
        // given
        PrivateChannelCreateRequest invalidRequest = new PrivateChannelCreateRequest(
            Arrays.asList());

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
        UUID channelId = UUID.randomUUID();
        PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("updatedChannel",
            "updated Description");

        ChannelDto responseDto = new ChannelDto(
            channelId,
            ChannelType.PUBLIC,
            "Updated Channel",
            "Updated description",
            Arrays.asList(),
            null
        );

        given(channelService.update(channelId, request)).willReturn(responseDto);

        // when & then
        mockMvc.perform(patch("/api/channels/{channelId}", channelId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(channelId.toString()))
            .andExpect(jsonPath("$.name").value("Updated Channel"))
            .andExpect(jsonPath("$.description").value("Updated description"));
    }

    @Test
    @DisplayName("채널 삭제 - 성공")
    void deleteChannel_Success() throws Exception {
        // given
        UUID channelId = UUID.randomUUID();
        willDoNothing().given(channelService).delete(channelId);

        // when & then
        mockMvc.perform(delete("/api/channels/{channelId}", channelId))
            .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("사용자별 채널 목록 조회 - 성공")
    void findAllChannelsByUserId_Success() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        List<ChannelDto> channels = Arrays.asList(
            new ChannelDto(
                UUID.randomUUID(),
                ChannelType.PUBLIC,
                "Channel 1",
                "Description 1",
                Arrays.asList(),
                Instant.now().minusSeconds(3600)
            ),
            new ChannelDto(
                UUID.randomUUID(),
                ChannelType.PRIVATE,
                null,
                null,
                Arrays.asList(
                    new UserDto(UUID.randomUUID(), "user1", "user1@example.com", null, true),
                    new UserDto(userId, "currentUser", "current@example.com", null, true)
                ),
                Instant.now().minusSeconds(1800)
            )
        );

        given(channelService.findAllByUserId(userId)).willReturn(channels);

        // when & then
        mockMvc.perform(get("/api/channels")
                .param("userId", userId.toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].name").value("Channel 1"))
            .andExpect(jsonPath("$[0].type").value("PUBLIC"))
            .andExpect(jsonPath("$[1].type").value("PRIVATE"));
    }

    @Test
    @DisplayName("사용자별 채널 목록 조회 - 빈 결과")
    void findAllChannelsByUserId_Empty() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        given(channelService.findAllByUserId(userId)).willReturn(Arrays.asList());

        // when & then
        mockMvc.perform(get("/api/channels")
                .param("userId", userId.toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("사용자별 채널 목록 조회 - 실패 (userId 파라미터 누락)")
    void findAllChannelsByUserId_MissingParameter() throws Exception {
        // when & then
        mockMvc.perform(get("/api/channels"))
            .andExpect(status().isBadRequest());
    }
}