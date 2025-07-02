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
import com.sprint.mission.discodeit.dto.Channel.ChannelDto;
import com.sprint.mission.discodeit.dto.Channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.Channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.Channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.User.UserDto;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.service.ChannelService;
import java.time.Instant;
import java.util.ArrayList;
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
@DisplayName("ChannelController 슬라이스 테스트")
public class ChannelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ChannelService channelService;

    @Test
    @DisplayName("공개 채널 생성 API가 정상적으로 동작한다.")
    void shouldCreatePublicChannel_whenValidRequest() throws Exception {

        // given
        String name = "공개 채널 생성 테스트";
        String description = "공개 채널 생성 테스트 채널입니다.";
        ChannelType channelType = ChannelType.PUBLIC;

        PublicChannelCreateRequest createRequest = new PublicChannelCreateRequest(name,
                description);
        ChannelDto channelDto = new ChannelDto(UUID.randomUUID(), name, description, channelType,
                Instant.now(), null);

        given(channelService.create(createRequest)).willReturn(channelDto);

        // when & then
        mockMvc.perform(post("/api/channels/public")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value(channelType.toString()))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.description").value(description));
    }

    @Test
    @DisplayName("유효하지 않은 입력으로 공개 채널 생성 시 400 에러가 발생한다.")
    void shouldReturnBadRequest_whenInvalidPublicChannelCreateRequest() throws Exception {

        // given
        String name = "";   // 이름을 빈 값으로 설정
        String description = "공개 채널 생성 테스트 채널입니다.";
        ChannelType channelType = ChannelType.PUBLIC;

        PublicChannelCreateRequest createRequest = new PublicChannelCreateRequest(name,
                description);

        // when & then
        mockMvc.perform(post("/api/channels/public")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("개인 채널 생성 API가 정상적으로 동작한다.")
    void shouldCreatePrivateChannel_whenValidRequest() throws Exception {

        // given
        ChannelType channelType = ChannelType.PRIVATE;
        UUID userId = UUID.randomUUID();
        UserDto userDto = new UserDto(userId, "테스트유저", "test@codeit.com", null, true);

        PrivateChannelCreateRequest createRequest = new PrivateChannelCreateRequest(
                List.of(userId));
        ChannelDto channelDto = new ChannelDto(UUID.randomUUID(), null, null, channelType,
                Instant.now(), List.of(userDto));

        given(channelService.create(createRequest)).willReturn(channelDto);

        // when & then
        mockMvc.perform(post("/api/channels/private")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value(channelType.toString()))
                .andExpect(jsonPath("$.participants[0].id").value(userId.toString()))
                .andExpect(jsonPath("$.participants[0].username").value("테스트유저"))
                .andExpect(jsonPath("$.participants[0].email").value("test@codeit.com"));
    }

    @Test
    @DisplayName("유효하지 않은 입력으로 개인 채널 생성 시 400 에러가 발생한다.")
    void shouldReturnBadRequest_whenInvalidPrivateChannelCreateRequest() throws Exception {

        // given
        ChannelType channelType = ChannelType.PRIVATE;

        PrivateChannelCreateRequest createRequest = new PrivateChannelCreateRequest(
                new ArrayList<>());

        // when & then
        mockMvc.perform(post("/api/channels/private")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("공개 채널 수정 API가 정상적으로 동작한다.")
    void shouldUpdatePublicChannel_whenValidRequest() throws Exception {

        // given
        String newName = "공개 채널 수정 테스트";
        String newDescription = "공개 채널 수정 테스트 채널입니다.";
        ChannelType channelType = ChannelType.PUBLIC;
        UUID channelId = UUID.randomUUID();

        PublicChannelUpdateRequest updateRequest = new PublicChannelUpdateRequest(newName,
                newDescription);

        ChannelDto channelDto = new ChannelDto(channelId, newName, newDescription, channelType,
                Instant.now(), null);

        given(channelService.update(channelId, updateRequest)).willReturn(channelDto);

        // when & then
        mockMvc.perform(patch("/api/channels/{channelId}", channelId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value(ChannelType.PUBLIC.toString()))
                .andExpect(jsonPath("$.name").value(newName))
                .andExpect(jsonPath("$.description").value(newDescription));
    }

    @Test
    @DisplayName("유효하지 않은 입력으로 공개 채널 수정 시 400 에러가 발생한다.")
    void shouldReturnBadRequest_whenInvalidPublicChannelUpdateRequest() throws Exception {

        // given
        String newName = "";
        String newDescription = "공개 채널 수정 테스트 채널입니다.";
        ChannelType channelType = ChannelType.PUBLIC;
        UUID channelId = UUID.randomUUID();

        PublicChannelUpdateRequest updateRequest = new PublicChannelUpdateRequest(newName,
                newDescription);
        ChannelDto channelDto = new ChannelDto(channelId, newName, newDescription, channelType,
                Instant.now(), null);

        given(channelService.update(channelId, updateRequest)).willReturn(channelDto);

        // when & then
        mockMvc.perform(patch("/api/channels/{channelId}", channelId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("채널 삭제 API가 정상적으로 동작한다.")
    void shouldDeleteChannel_whenValidChannelId() throws Exception {

        // given
        UUID channelId = UUID.randomUUID();

        willDoNothing().given(channelService).delete(channelId);

        // when & then
        mockMvc.perform(delete("/api/channels/{channelId}", channelId))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("특정 사용자가 조회 가능한 전체 채널 목록 조회 API가 정상적으로 동작한다.")
    void shouldReturnAllChannelsForUser_whenValidUserId() throws Exception {

        // given
        UUID userId = UUID.randomUUID();
        UserDto user = new UserDto(userId, "테스트유저", "test@codeit.com", null, true);

        ChannelDto channel1 = new ChannelDto(UUID.randomUUID(), "1번 채널", "1번 채널입니다.",
                ChannelType.PUBLIC, Instant.now(), null);
        ChannelDto channel2 = new ChannelDto(UUID.randomUUID(), "2번 채널", "2번 채널입니다.",
                ChannelType.PUBLIC, Instant.now(), null);
        ChannelDto channel3 = new ChannelDto(UUID.randomUUID(), null, null, ChannelType.PRIVATE,
                Instant.now(), List.of(user));

        List<ChannelDto> channels = List.of(channel1, channel2, channel3);

        given(channelService.findAllByUserId(userId)).willReturn(channels);

        // when & then
        mockMvc.perform(get("/api/channels").param("userId", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value(ChannelType.PUBLIC.toString()))
                .andExpect(jsonPath("$[0].name").value("1번 채널"))
                .andExpect(jsonPath("$[0].description").value("1번 채널입니다."))
                .andExpect(jsonPath("$[1].type").value(ChannelType.PUBLIC.toString()))
                .andExpect(jsonPath("$[1].name").value("2번 채널"))
                .andExpect(jsonPath("$[1].description").value("2번 채널입니다."))
                .andExpect(jsonPath("$[2].type").value(ChannelType.PRIVATE.toString()))
                .andExpect(jsonPath("$[2].participants[0].id").value(userId.toString()));
    }
}
