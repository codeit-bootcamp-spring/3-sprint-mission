package com.sprint.mission.discodeit.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(ChannelController.class)
@Import(GlobalExceptionHandler.class)
class ChannelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChannelService channelService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto(UUID.randomUUID(), "testUser", "test@example.com", null,
            null);
    }

    @Test
    void createChannel_success() throws Exception {
        // Given
        PublicChannelCreateRequest request = new PublicChannelCreateRequest("testChannel", "test");
        ChannelDto channelDto = new ChannelDto(UUID.randomUUID(), ChannelType.PUBLIC, "testChannel",
            "test", List.of(userDto), null);
        when(channelService.create(request)).thenReturn(channelDto);

        // When
        ResultActions result = mockMvc.perform(post("/api/channels/public")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)));

        // Then
        result.andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("testChannel"));

    }

    @Test
    @DisplayName("공개 채널 생성 실패 - 글자 수 조건 (20자 초과)")
    void createPublicChannelFail() throws Exception {
        // Given
        PublicChannelCreateRequest request = new PublicChannelCreateRequest(
            "abcdefghijklmnopqrstuwxyz", "");

        // When
        ResultActions result = mockMvc.perform(post("/api/channels/public")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)));

        // Then
        result.andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("INVALID_REQUEST"))
            .andExpect(jsonPath("$.message").value("요청 데이터가 유효하지 않습니다."));
    }

    @Test
    @DisplayName("비공개 채널 생성 성공")
    void createPrivateChannel_success() throws Exception {
        // Given
        PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(
            List.of(userDto.id()));
        ChannelDto channelDto = new ChannelDto(UUID.randomUUID(), ChannelType.PRIVATE, null, null,
            List.of(userDto), null);
        when(channelService.create(request)).thenReturn(channelDto);

        // When
        ResultActions result = mockMvc.perform(post("/api/channels/private")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)));

        // Then
        result.andExpect(status().isCreated())
            .andExpect(jsonPath("$.type").value("PRIVATE"));
    }

    @Test
    @DisplayName("채널 삭제 API 성공")
    void delete_success() throws Exception {
        // Given
        UUID channelId = UUID.randomUUID();
        doNothing().when(channelService).delete(channelId);

        // When
        ResultActions result = mockMvc.perform(delete("/api/channels/{channelId}", channelId));

        // Then
        result.andExpect(status().isNoContent());
        verify(channelService).delete(channelId);
    }

    @Test
    @DisplayName("채널 삭제 실패 - 존재하지 않는 채널")
    void deleteChannelFail() throws Exception {
        // Given
        UUID channelId = UUID.randomUUID();
        doThrow(new ChannelNotFoundException(channelId)).when(channelService).delete(channelId);

        // When
        ResultActions result = mockMvc.perform(delete("/api/channels/{channelId}", channelId));

        // Then
        result.andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("사용자의 채널 전체 조회")
    void findAllChannelsByUserId() throws Exception {
        // Given
        UUID userId = UUID.randomUUID();
        ChannelDto channel1 = new ChannelDto(UUID.randomUUID(), ChannelType.PUBLIC, "testChannel1",
            "description1", null, null);
        ChannelDto channel2 = new ChannelDto(UUID.randomUUID(), ChannelType.PUBLIC, "testChannel2",
            "description2", null, null);
        when(channelService.findAllByUserId(userId)).thenReturn(List.of(channel1, channel2));

        // When
        ResultActions result = mockMvc.perform(get("/api/channels")
            .param("userId", userId.toString()));

        // Then
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2));
    }
}