package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.service.ChannelService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@WebMvcTest(
    controllers = ChannelController.class,
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = GlobalExceptionHandler.class
    )
)
@WithMockUser
@DisplayName("ChannelController 슬라이스 테스트")
public class ChannelControllerTest {

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
                .with(csrf()))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(respDto.id().toString()))
            .andExpect(jsonPath("$.name").value("public"))
            .andExpect(jsonPath("$.type").value(respDto.type().toString()));

        then(channelService).should(times(1))
            .create(any(PublicChannelCreateRequest.class));
    }

    @Test
    @DisplayName("비공개 채널 생성 API")
    void createPrivateChannel() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        UserDto userDto =  new UserDto(userId, "tom", "tom@test.com", null, false, Role.USER);

        PrivateChannelCreateRequest req = new PrivateChannelCreateRequest(List.of(userId));
        ChannelDto channelDto = new ChannelDto(
            UUID.randomUUID(),
            ChannelType.PRIVATE,
            null,
            null,
            List.of(userDto),
            Instant.now()
        );
        given(channelService.create(any(PrivateChannelCreateRequest.class))).willReturn(channelDto);

        // when, then
        mockMvc.perform(post("/api/channels/private")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))
                .with(csrf()))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(channelDto.id().toString()))
            .andExpect(jsonPath("$.name").value(channelDto.name()))
            .andExpect(jsonPath("$.type").value(channelDto.type().toString()));

        then(channelService).should(times(1))
            .create(any(PrivateChannelCreateRequest.class));
    }

    @Test
    @DisplayName("채널 정보 수정 API")
    void updateChannel() throws Exception {
        // given
        UUID channelId = UUID.randomUUID();
        PublicChannelUpdateRequest req = new PublicChannelUpdateRequest("publicChannel", "public Channel.");

        ChannelDto channelDto = new ChannelDto(channelId, ChannelType.PUBLIC, "publicChannel", "public Channel.", null, Instant.MIN);
        given(channelService.update(eq(channelId), any(PublicChannelUpdateRequest.class))).willReturn(channelDto);

        // when, then
        mockMvc.perform(patch("/api/channels/{channelId}", channelId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(channelId.toString()))
            .andExpect(jsonPath("$.name").value(channelDto.name()))
            .andExpect(jsonPath("$.description").value(channelDto.description()));

        then(channelService).should(times(1))
            .update(eq(channelId), any(PublicChannelUpdateRequest.class));
    }

    @Test
    @DisplayName("채널 삭제 API")
    void deleteChannel() throws Exception {
        // given
        UUID channelId = UUID.randomUUID();
        willDoNothing().given(channelService).delete(channelId);

        // when, then
        mockMvc.perform(delete("/api/channels/{channelId}", channelId)
                .with(csrf()))
            .andExpect(status().isNoContent());

        then(channelService).should(times(1)).delete(channelId);
    }
}