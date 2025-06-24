package com.sprint.mission.discodeit.slice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.controller.ChannelController;
import com.sprint.mission.discodeit.dto.channel.request.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.response.JpaChannelResponse;
import com.sprint.mission.discodeit.dto.user.JpaUserResponse;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.exception.UserNotFoundException;
import com.sprint.mission.discodeit.unit.basic.BasicChannelService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.*;

import static org.hamcrest.Matchers.hasKey;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * PackageName  : com.sprint.mission.discodeit.slice.controller
 * FileName     : ChannelControllerTest
 * Author       : dounguk
 * Date         : 2025. 6. 21.
 */
@WebMvcTest(controllers = ChannelController.class)
@DisplayName("Channel Controller 슬라이스 테스트")
public class ChannelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BasicChannelService channelService;

    @Test
    @DisplayName("Public 채널 생성 API가 정상적으로 동작한다.")
    void createPublicChannel_Success() throws Exception {
        // given
        PublicChannelCreateRequest request = new PublicChannelCreateRequest("Test channel", "Test channel description");

        List<JpaUserResponse> participants = new ArrayList<>();
        JpaUserResponse participant = JpaUserResponse.builder().build();
        participants.add(participant);

        UUID id = UUID.randomUUID();
        JpaChannelResponse response = JpaChannelResponse.builder()
            .id(id)
            .type(ChannelType.PUBLIC)
            .name("Test channel")
            .description("Test channel description")
            .participants(participants)
            .lastMessageAt(Instant.MAX)
            .build();

        given(channelService.createChannel(any(PublicChannelCreateRequest.class)))
            .willReturn(response);

        // when n then
        mockMvc.perform(post("/api/channels/public")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(id.toString()))
            .andExpect(jsonPath("$.type").value(ChannelType.PUBLIC.name()))
            .andExpect(jsonPath("$.name").value("Test channel"))
            .andExpect(jsonPath("$.description").value("Test channel description"))
            .andExpect(jsonPath("$.participants.size()").value(1))
            .andExpect(jsonPath("$.lastMessageAt").value(Instant.MAX.toString()));
    }

    @Test
    @DisplayName("public 채널에 이름이 없을경우 생성되지 않는다. ")
    void createChannel_InvalidInput_BadRequest() throws Exception{
        // given
        PublicChannelCreateRequest request = new PublicChannelCreateRequest(null, "Test channel description");

        // when n then
        mockMvc.perform(post("/api/channels/public")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Private 채널 생성 API가 정상적으로 동작한다.")
    void createPrivateChannel_success() throws Exception {
        // given
        User user1 = new User("paul", "paul@gmail.com", "1234");
        User user2 = new User("daniel", "daniel@gmail.com", "1234");

        Set<UUID> participants = new HashSet<>();
        participants.add(user1.getId());
        participants.add(user2.getId());

        PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(participants);

        UUID id = UUID.randomUUID();
        Instant lastMessageAt = Instant.now();

        JpaUserResponse userResponse1 = JpaUserResponse.builder()
            .username("paul")
            .id(user1.getId())
            .email("paul@gmail.com")
            .build();

        JpaUserResponse userResponse2 = JpaUserResponse.builder()
            .username("daniel")
            .id(user2.getId())
            .email("daniel@gmail.com")
            .build();

        List<JpaUserResponse> participantsList = new ArrayList<>();
        participantsList.add(userResponse1);
        participantsList.add(userResponse2);

        JpaChannelResponse response = JpaChannelResponse.builder()
            .id(id)
            .type(ChannelType.PRIVATE)
            .name("")
            .description("")
            .participants(participantsList)
            .lastMessageAt(lastMessageAt)
            .build();

        given(channelService.createChannel(any(PrivateChannelCreateRequest.class))).willReturn(response);

        // when n then
        mockMvc.perform(post("/api/channels/private")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(id.toString()))
            .andExpect(jsonPath("$.type").value(ChannelType.PRIVATE.name()))
            .andExpect(jsonPath("$.name").value(""))
            .andExpect(jsonPath("$.description").value(""))
            .andExpect(jsonPath("$.participants.size()").value(2))
            .andExpect(jsonPath("$.participants").isArray())
            .andExpect(jsonPath("$.lastMessageAt").value(lastMessageAt.toString()));
    }

    @Test
    @DisplayName("참여 유저 수가 2명 미만일 경우 에러가 발생한다.")
    void createPrivateChannel_notEnoughUsers_NotFound() throws Exception {
        // given
        User user = new User("paul", "paul@gmail.com", "1234");

        Set<UUID> participantsList = new HashSet<>();
        participantsList.add(user.getId());

        PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(participantsList);

        given(channelService.createChannel(any(PrivateChannelCreateRequest.class)))
            .willThrow(new UserNotFoundException(Map.of("users", "not enough users in private channel")));

        // when n then
        mockMvc.perform(post("/api/channels/private")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.details").isMap())
            .andExpect(jsonPath("$.message").value("유저를 찾을 수 없습니다."))
            .andExpect(jsonPath("$.details", hasKey("users")))
            .andExpect(jsonPath("$.details.users").value("not enough users in private channel"));
    }

    @Test
    @DisplayName("채널 삭제 API가 정상 작동한다.")
    void channel_delete_success() throws Exception {
        // given
        UUID channelId = UUID.randomUUID();

        // when n then
        mockMvc.perform(delete("/api/channels/{channelId}", channelId))
            .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("채널을 찾을 수 없을 경우 ChannelNotFound(404) 에러를 만든다.")
    void deleteChannel_noUserExists_channelNotFound() throws Exception {
        // given
        UUID channelId = UUID.randomUUID();

        doThrow(new ChannelNotFoundException(Map.of("channelId", channelId)))
            .when(channelService).deleteChannel(any(UUID.class));

        // when n then
        mockMvc.perform(delete("/api/channels/{channelId}", channelId))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("채널을 찾을 수 없습니다."))
            .andExpect(jsonPath("$.details").isMap())
            .andExpect(jsonPath("$.details", hasKey("channelId")))
            .andExpect(jsonPath("$.details.channelId").value(channelId.toString()));
    }

    @Test
    @DisplayName("퍼블릭 체널 수정 API가 정상 작동한다.")
    void updatePublicChannel_success() throws Exception {
        // given
        UUID channelId = UUID.randomUUID();
        Instant lastMessageAt = Instant.now();
        ChannelUpdateRequest request = new ChannelUpdateRequest("new channel name","new description");

        JpaChannelResponse response = JpaChannelResponse.builder()
            .id(channelId)
            .type(ChannelType.PUBLIC)
            .name("new channel name")
            .description("new description")
            .lastMessageAt(lastMessageAt)
            .build();

        given(channelService.update(any(UUID.class), any(ChannelUpdateRequest.class))).willReturn(response);

        // when n then
        mockMvc.perform(patch("/api/channels/{channelId}", channelId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(channelId.toString()))
            .andExpect(jsonPath("$.type").value(ChannelType.PUBLIC.name()))
            .andExpect(jsonPath("$.name").value("new channel name"))
            .andExpect(jsonPath("$.description").value("new description"))
            .andExpect(jsonPath("$.lastMessageAt").value(lastMessageAt.toString()));
    }

    @Test
    @DisplayName("프라이빗 채널 수정을 시도하면 PrivateChannelUpdate(400) 에러를 만든다.")
    void updateChannel_private_PrivateCHannelUpdateException() throws Exception {
        // given
        UUID channelId = UUID.randomUUID();

        ChannelUpdateRequest request = new ChannelUpdateRequest("new channel name","new description");

        given(channelService.update(any(UUID.class), any(ChannelUpdateRequest.class)))
            .willThrow(new PrivateChannelUpdateException(Map.of("channelId", channelId.toString())));

        // when n then
        mockMvc.perform(patch("/api/channels/{channelId}", channelId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("프라이빗 채널은 수정이 불가능합니다."))
            .andExpect(jsonPath("$.details").isMap())
            .andExpect(jsonPath("$.details", hasKey("channelId")))
            .andExpect(jsonPath("$.details.channelId").value(channelId.toString()));
    }

    @Test
    @DisplayName("채널을 찾는 API가 정상 작동한다.")
    void findChannelsById_success() throws Exception {
        // given
        UUID userId = UUID.randomUUID();

        JpaChannelResponse response1 = JpaChannelResponse.builder().build();
        JpaChannelResponse response2 = JpaChannelResponse.builder().build();

        List<JpaChannelResponse> responseList = Arrays.asList(response1, response2);

        given(channelService.findAllByUserId(any(UUID.class))).willReturn(responseList);

        // when n then
        mockMvc.perform(get("/api/channels")
                .param("userId",userId.toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isNotEmpty())
            .andExpect(jsonPath("$.size()").value(responseList.size()));
    }

    @Test
    @DisplayName("유저 정보가 없을경우 빈 리스트를 반환한다.")
    void findChannels_noChannels_emptyList() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        given(channelService.findAllByUserId(any(UUID.class))).willReturn(Collections.emptyList());

        // when
        mockMvc.perform(get("/api/channels")
                .param("userId", userId.toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }
}
