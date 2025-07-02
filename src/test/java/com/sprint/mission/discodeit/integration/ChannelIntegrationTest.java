package com.sprint.mission.discodeit.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("채널 API 통합 테스트")
public class ChannelIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("공개 채널 생성 시 Repository까지 반영되어야 한다")
    void createPublicChannel_Integration_Success() throws Exception {
        // Given
        PublicChannelCreateRequest publicChannelCreateRequest = new PublicChannelCreateRequest(
            "testPublicChannel",
            "test description"
        );

        // When & Then
        mockMvc.perform(post("/api/channels/public")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(publicChannelCreateRequest)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("testPublicChannel"))
            .andExpect(jsonPath("$.description").value("test description"))
            .andExpect(jsonPath("$.type").value("PUBLIC"));

        assertThat(channelRepository.findAll()).hasSize(1);
        Channel savedChannel = channelRepository.findAll().get(0);
        assertThat(savedChannel.getName()).isEqualTo("testPublicChannel");
        assertThat(savedChannel.getType()).isEqualTo(ChannelType.PUBLIC);
        assertThat(savedChannel.getDescription()).isEqualTo("test description");

    }

    @Test
    @DisplayName("공개 채널 생성 시 사용자명이 짧으면 400이 반환되어야 한다")
    void createPublicChannel_Integration_ValidationFailure() throws Exception {
        // Given
        PublicChannelCreateRequest request = new PublicChannelCreateRequest(
            "",
            "General discussion channel"
        );

        // When & Then
        mockMvc.perform(post("/api/channels/public")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());

        assertThat(channelRepository.findAll()).isEmpty();

    }

    @Test
    @DisplayName("프라이빗 채널 생성 시 Repository까지 반영되어야 한다")
    void createPrivateChannel_Integration_Success() throws Exception {
        // Given
        UUID firstUserId = createUserAndGetId("firstUser", "first@example.com");
        UUID secondUserId = createUserAndGetId("secondUser", "second@example.com");

        PrivateChannelCreateRequest privateChannelCreateRequest = new PrivateChannelCreateRequest(
            List.of(firstUserId, secondUserId)
        );

        // When & Then
        mockMvc.perform(post("/api/channels/private")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(privateChannelCreateRequest)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.type").value(ChannelType.PRIVATE.name()));

        assertThat(channelRepository.findAll()).hasSize(1);
        Channel savedChannel = channelRepository.findAll().get(0);
        assertThat(savedChannel.getType()).isEqualTo(ChannelType.PRIVATE);

    }

    @Test
    @DisplayName("공개 채널 수정 시 Repository까지 반영되어야 한다")
    void updatePublicChannel_Integration_Success() throws Exception {
        // Given
        UUID channelId = createPublicChannelAndGetId("general", "General discussion channel");

        PublicChannelUpdateRequest request = new PublicChannelUpdateRequest(
            "updated-general",
            "Updated general discussion channel"
        );

        // When & Then
        mockMvc.perform(patch("/api/channels/{channelId}", channelId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("updated-general"))
            .andExpect(jsonPath("$.description").value("Updated general discussion channel"));

        Channel updatedChannel = channelRepository.findById(channelId).orElseThrow();
        assertThat(updatedChannel.getName()).isEqualTo("updated-general");
        assertThat(updatedChannel.getDescription()).isEqualTo("Updated general discussion channel");
    }

    @Test
    @DisplayName("프라이빗 채널 수정 시 프라이빗 수정 커스텀 예외가 발생한다")
    void updatePrivateChannel_Integration_PrivateChannelModificationException_Failure()
        throws Exception {
        // Given
        UUID firstUserId = createUserAndGetId("firstUser", "first@example.com");
        UUID secondUserId = createUserAndGetId("secondUser", "second@example.com");

        UUID channelId = createPrivateChannelAndGetId(List.of(firstUserId, secondUserId));

        PublicChannelUpdateRequest request = new PublicChannelUpdateRequest(
            "updated-general",
            "Updated general discussion channel"
        );

        // When & Then
        mockMvc.perform(patch("/api/channels/{channelId}", channelId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());

        Channel updatedChannel = channelRepository.findById(channelId).orElseThrow();
        assertThat(updatedChannel.getName()).isNotEqualTo("updated-general");
        assertThat(updatedChannel.getDescription()).isNotEqualTo(
            "Updated general discussion channel");
    }

    @Test
    @DisplayName("존재하지 않는 채널 수정 시 404가 반환되어야 한다")
    void updateChannel_Integration_NotFound_Failure() throws Exception {
        // Given
        PublicChannelUpdateRequest request = new PublicChannelUpdateRequest(
            "nonexistent",
            "This channel does not exist"
        );

        // When & Then
        mockMvc.perform(patch("/api/channels/{channelId}", UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("채널을 삭제하면 Repository에서 제거되어야 한다")
    void deleteChannel_Integration_Success() throws Exception {
        // Given
        UUID channelId = createPublicChannelAndGetId("delete-channel", "Channel to delete");

        // When & Then
        mockMvc.perform(delete("/api/channels/{channelId}", channelId))
            .andExpect(status().isNoContent());

        boolean exists = channelRepository.existsById(channelId);
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("존재하지 않는 채널을 삭제하면 404 Not Found가 발생해야 한다")
    void deleteChannel_Integration_NotFound() throws Exception {
        // Given
        UUID channelId = UUID.randomUUID();

        // When & Then
        mockMvc.perform(delete("/api/channels/{channelId}", channelId))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("채널 목록 조회 시 전체 채널이 반환되어야 한다")
    void getChannels_Integration_Success() throws Exception {
        // Given
        UUID firstUserId = createUserAndGetId("firstUser", "first@example.com");
        UUID secondUserId = createUserAndGetId("secondUser", "second@example.com");

        createPublicChannelAndGetId("general", "General channel");
        createPrivateChannelAndGetId(List.of(firstUserId, secondUserId));

        // When & Then
        mockMvc.perform(get("/api/channels")
                .param("userId", firstUserId.toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2));

        assertThat(channelRepository.findAll()).hasSize(2);
    }

    @Test
    @DisplayName("등록된 채널이 없을 경우 빈 배열이 반환되어야 한다")
    void getChannels_EmptyList_Integration() throws Exception {
        // Given
        UUID testUserId = createUserAndGetId("testuser", "test@example.com");

        // When & Then
        mockMvc.perform(get("/api/channels")
                .param("userId", testUserId.toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(0));
    }

    private UUID createPublicChannelAndGetId(String name, String description) throws Exception {
        PublicChannelCreateRequest request = new PublicChannelCreateRequest(name, description);

        String body = mockMvc.perform(post("/api/channels/public")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andReturn()
            .getResponse()
            .getContentAsString();

        return UUID.fromString(JsonPath.read(body, "$.id"));
    }

    private UUID createPrivateChannelAndGetId(List<UUID> participantIds) throws Exception {
        PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(participantIds);

        String body = mockMvc.perform(post("/api/channels/private")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andReturn()
            .getResponse()
            .getContentAsString();

        return UUID.fromString(JsonPath.read(body, "$.id"));
    }

    private UUID createUserAndGetId(String username, String email) {
        User user = new User(username, email, "!password123", null);

        initializeUserStatus(user);

        User savedUser = userRepository.save(user);
        return savedUser.getId();
    }

    private void initializeUserStatus(User user) {
        UserStatus userStatus = new UserStatus(user, Instant.now());
    }


}
