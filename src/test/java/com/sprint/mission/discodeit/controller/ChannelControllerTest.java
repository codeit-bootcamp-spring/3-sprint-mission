package com.sprint.mission.discodeit.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@WebMvcTest(ChannelController.class)
@Import({GlobalExceptionHandler.class})
public class ChannelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ChannelService channelService;

    @BeforeEach
    @DisplayName("테스트 환경 설정 확인")
    void setUp() {

        assert mockMvc != null;
        assert objectMapper != null;
        assert channelService != null;
    }

    @Nested
    @DisplayName("공개 채널 생성")
    class CreatePublicChannelTests {

        @Test
        @DisplayName("공개 채널 생성 성공")
        void createPublicChannel_Success() throws Exception {
            // Given
            PublicChannelCreateRequest publicChannelCreateRequest = new PublicChannelCreateRequest(
                "공개채널", "채널 설명");

            ChannelDto response = new ChannelDto(UUID.randomUUID(), ChannelType.PUBLIC, "공개채널",
                "채널설명", null, null);

            given(channelService.create(publicChannelCreateRequest)).willReturn(response);

            // When & Then
            mockMvc.perform(post("/api/channels/public")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(publicChannelCreateRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value(ChannelType.PUBLIC.name()))
                .andExpect(jsonPath("$.name").value("공개채널"))
                .andExpect(jsonPath("$.description").value("채널설명"));
        }
    }

    @Nested
    @DisplayName("비공개 채널 생성")
    class CreatePrivateChannelTests {

        @Test
        @DisplayName("비공개 채널 생성 성공")
        void createPrivateChannel_Success() throws Exception {
            //Given
            List<UUID> participantIds = List.of(UUID.randomUUID(), UUID.randomUUID());

            PrivateChannelCreateRequest privateChannelCreateRequest
                = new PrivateChannelCreateRequest(participantIds);

            ChannelDto response = new ChannelDto(UUID.randomUUID(), ChannelType.PRIVATE, null, null,
                null, null);

            given(channelService.create(privateChannelCreateRequest)).willReturn(response);

            // When & Then
            mockMvc.perform(post("/api/channels/private")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(privateChannelCreateRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value(ChannelType.PRIVATE.name()));
        }
    }

    @Nested
    @DisplayName("공개 채널 수정")
    class ChannelUpdate {

        @Test
        @DisplayName("공개 채널 수정 성공")
        void updatePublicChannel_Success() throws Exception {
            // Given
            UUID channelId = UUID.randomUUID();

            PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("새 채널명", "수정된 설명");

            ChannelDto response = new ChannelDto(UUID.randomUUID(), ChannelType.PUBLIC, "새 채널명",
                "채널설명", null, null);

            given(channelService.update(channelId, request)).willReturn(response);

            // When & Then
            mockMvc.perform(patch("/api/channels/{channelId}", channelId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value(ChannelType.PUBLIC.name()))
                .andExpect(jsonPath("$.name").value("새 채널명"))
                .andExpect(jsonPath("$.description").value("채널설명"));
        }


        @Test
        @DisplayName("공개 채널 수정 실패 - 존재하지 않는 채널")
        void updatePublicChannel_Fail_ChannelNotFound() throws Exception {
            // Given
            UUID channelId = UUID.randomUUID();
            PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("수정채널", "수정 설명");

            given(channelService.update(channelId, request))
                .willThrow(new ChannelNotFoundException(channelId));

            // When & Then
            mockMvc.perform(patch("/api/channels/{channelId}", channelId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Channel을 찾을 수 없습니다."))
                .andExpect(jsonPath("$.details.channelId").value(channelId.toString()));
        }
    }

    @Nested
    @DisplayName("채널 삭제")
    class ChannelDelete {

        @Test
        @DisplayName("채널 삭제 성공")
        void deleteChannel_Success() throws Exception {
            //Given
            UUID channelId = UUID.randomUUID();

            doNothing().when(channelService).delete(channelId);

            // When & Then
            mockMvc.perform(delete("/api/channels/{channelId}", channelId))
                .andExpect(status().isNoContent());
        }
    }

    @Test
    @DisplayName("채널 삭제 실패 - 존재하지 않는 채널")
    void deleteChannel_Fail_ChannelNotFound() throws Exception {
        // Given
        UUID channelId = UUID.randomUUID();

        willThrow(new ChannelNotFoundException(channelId))
            .given(channelService).delete(channelId);

        // When & Then
        mockMvc.perform(delete("/api/channels/{channelId}", channelId))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Channel을 찾을 수 없습니다."))
            .andExpect(jsonPath("$.details.channelId").value(channelId.toString()));
    }

}
