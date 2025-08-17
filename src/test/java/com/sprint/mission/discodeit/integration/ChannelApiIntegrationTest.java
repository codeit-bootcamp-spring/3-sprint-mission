package com.sprint.mission.discodeit.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ChannelApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ChannelService channelService;

    @Autowired
    private UserService userService;

    private PublicChannelCreateRequest publicChannelCreateRequest;
    private PublicChannelUpdateRequest publicChannelUpdateRequest;
    private UserCreateRequest userCreateRequest;

    @BeforeEach
    void setUp() {
        publicChannelCreateRequest = new PublicChannelCreateRequest("공개 채널", "공개 채널 입니다.");
        publicChannelUpdateRequest = new PublicChannelUpdateRequest("newChannelName",
                "newChannelDescription");
        userCreateRequest = new UserCreateRequest("testuser", "test@abc.com", "1234");

    }

    @Test
    @WithMockUser(roles = "CHANNEL_MANAGER")
    @DisplayName("공개 채널 생성 성공")
    void createPublicChannel_Success() throws Exception {
        mockMvc.perform(post("/api/channels/public")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(publicChannelCreateRequest))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.type").value("PUBLIC"))
                .andExpect(jsonPath("$.name").value("공개 채널"))
                .andExpect(jsonPath("$.description").value("공개 채널 입니다."));
    }

    @Test
    @WithMockUser(roles = "CHANNEL_MANAGER")
    @DisplayName("공개 채널 생성 실패")
    void createPublicChannel_Failure() throws Exception {
        PublicChannelCreateRequest invalidRequest = new PublicChannelCreateRequest("a", "");

        mockMvc.perform(post("/api/channels/public")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest))
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }


    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("비공개 채널 생성 성공")
    void createPrivateChannel_Success() throws Exception {
        UserDto user1 = userService.createUser(userCreateRequest, Optional.empty());

        List<UUID> participantIds = List.of(user1.id());
        PrivateChannelCreateRequest createRequest = new PrivateChannelCreateRequest(participantIds);

        mockMvc.perform(post("/api/channels/private")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.type").value("PRIVATE"))
                .andExpect(jsonPath("$.participants", hasSize(1)));
    }

    @Test
    @WithMockUser(roles = "CHANNEL_MANAGER")
    @DisplayName("사용자별 채널 조회 성공")
    void findAllChannelsByUserId_Success() throws Exception {
        UserDto user = userService.createUser(userCreateRequest, Optional.empty());
        UUID userId = user.id();

        channelService.create(publicChannelCreateRequest);

        PrivateChannelCreateRequest privateChannelRequest = new PrivateChannelCreateRequest(
                List.of(userId));

        channelService.create(privateChannelRequest);

        mockMvc.perform(get("/api/channels")
                        .param("userId", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].type").value("PUBLIC"))
                .andExpect(jsonPath("$[1].type").value("PRIVATE"));
    }

    @Test
    @WithMockUser(roles = "CHANNEL_MANAGER")
    @DisplayName("채널 수정 성공")
    void updateChannel_Success() throws Exception {
        ChannelDto createdChannel = channelService.create(publicChannelCreateRequest);
        UUID channelId = createdChannel.id();

        mockMvc.perform(patch("/api/channels/{channelId}", channelId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(publicChannelUpdateRequest))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(channelId.toString()))
                .andExpect(jsonPath("$.name").value("newChannelName"))
                .andExpect(jsonPath("$.description").value("newChannelDescription"));
    }

    @Test
    @WithMockUser(roles = "CHANNEL_MANAGER")
    @DisplayName("채널 수정 실패")
    void updateChannel_Failure() throws Exception {
        UUID nonExistentChannelId = UUID.randomUUID();

        mockMvc.perform(patch("/api/channels/{channelId}", nonExistentChannelId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(publicChannelUpdateRequest))
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "CHANNEL_MANAGER")
    @DisplayName("채널 삭제 성공")
    void deleteChannel_Success() throws Exception {
        ChannelDto createdChannel = channelService.create(publicChannelCreateRequest);
        UUID channelId = createdChannel.id();

        mockMvc.perform(delete("/api/channels/{channelId}", channelId)
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "CHANNEL_MANAGER")
    @DisplayName("채널 삭제 실패")
    void deleteChannel_Failure() throws Exception {
        UUID nonExistentChannelId = UUID.randomUUID();

        mockMvc.perform(delete("/api/channels/{channelId}", nonExistentChannelId)
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

}