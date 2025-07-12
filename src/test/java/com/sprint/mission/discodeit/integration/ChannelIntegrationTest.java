package com.sprint.mission.discodeit.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ChannelIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserStatusRepository userStatusRepository;
    @Autowired
    private ChannelRepository channelRepository;
    @Autowired
    private ReadStatusRepository readStatusRepository;

    @Test
    @DisplayName("공개 채널 생성 성공")
    void createPublicChannel_success() throws Exception {
        // Given
        PublicChannelCreateRequest request = new PublicChannelCreateRequest("General",
            "Public channel");

        // When
        ResultActions result = mockMvc.perform(post("/api/channels/public")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)));

        // Then
        result.andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("General"))
            .andExpect(jsonPath("$.description").value("Public channel"));
    }

    @Test
    @DisplayName("비공개 채널 생성 성공")
    void createPrivateChannel_success() throws Exception {
        // Given
        User user1 = new User("user1", "u1@example.com", "pass", null);
        User user2 = new User("user2", "u2@example.com", "pass", null);
        userRepository.saveAll(List.of(user1, user2));

        userStatusRepository.saveAll(List.of(
            new UserStatus(user1, Instant.now()),
            new UserStatus(user2, Instant.now())
        ));

        PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(
            List.of(user1.getId(), user2.getId())
        );

        // When
        ResultActions result = mockMvc.perform(post("/api/channels/private")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)));

        // Then
        result.andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.participants.length()").value(2));
    }

    @Test
    @DisplayName("공개 채널 수정 성공")
    void updatePublicChannel_success() throws Exception {
        // Given
        Channel channel = new Channel(ChannelType.PUBLIC, "OldName", "OldDescription");
        channelRepository.save(channel);

        PublicChannelUpdateRequest updateRequest = new PublicChannelUpdateRequest("NewName",
            "NewDesc");

        // When
        ResultActions result = mockMvc.perform(patch("/api/channels/" + channel.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateRequest)));

        // Then
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("NewName"))
            .andExpect(jsonPath("$.description").value("NewDesc"));
    }

    @Test
    @DisplayName("공개 채널 삭제 성공")
    void deleteChannel_success() throws Exception {
        // Given
        Channel channel = new Channel(ChannelType.PUBLIC, "ToDelete", "...");
        channelRepository.save(channel);

        // When
        ResultActions result = mockMvc.perform(delete("/api/channels/" + channel.getId()));

        // Then
        result.andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("사용자 기준 채널 목록 조회 성공")
    void findAllChannelsByUserId_success() throws Exception {
        // Given
        User user = new User("subUser", "sub@example.com", "pass", null);
        userRepository.save(user);
        userStatusRepository.save(new UserStatus(user, Instant.now()));

        Channel publicChannel = new Channel(ChannelType.PUBLIC, "open", "desc");
        channelRepository.save(publicChannel);

        // When
        ResultActions result = mockMvc.perform(get("/api/channels")
            .param("userId", user.getId().toString()));

        // Then
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$[?(@.name == 'open')]").exists());
    }
}