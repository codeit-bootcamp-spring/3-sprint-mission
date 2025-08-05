package com.sprint.mission.discodeit.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
@DisplayName("Channel API 통합 테스트")
public class ChannelIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ChannelService channelService;

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserStatusRepository userStatusRepository;

    private Channel savedPublicChannel;
    private Channel savedPrivateChannel;
    private User savedUser;

    @BeforeEach
    void setUp() {
        // 공개 채널 생성
        Channel publicChannel = new Channel(ChannelType.PUBLIC, "공개채널테스트", "공개 채널 테스트입니다.");
        savedPublicChannel = channelRepository.save(publicChannel);

        // 사용자 생성
        User user = new User("테스트유저", "test@codeit.com", "test1234", null);
        savedUser = userRepository.save(user);

        // UserStatus 생성 및 연결
        UserStatus userStatus = new UserStatus(savedUser, java.time.Instant.now());
        userStatusRepository.save(userStatus);

        // 유저가 참여한 비공개 채널 생성
        PrivateChannelCreateRequest createRequest = new PrivateChannelCreateRequest(
            List.of(savedUser.getId()));
        ChannelDto savedPrivateChannelDto = channelService.create(createRequest);
        savedPrivateChannel = channelRepository.findById(savedPrivateChannelDto.id()).orElse(null);
    }

    @Test
    @DisplayName("공개 채널 생성")
    void createPublicChannel_Success() throws Exception {
        // Given
        String name = "공개 채널 생성 테스트";
        String description = "공개 채널 생성 테스트 채널입니다.";
        PublicChannelCreateRequest createRequest = new PublicChannelCreateRequest(name,
            description);

        // When
        ResultActions result = mockMvc.perform(post("/api/channels/public")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createRequest)));

        // Then
        result.andExpect(status().isCreated())
            .andExpect(jsonPath("$.type").value(ChannelType.PUBLIC.toString()))
            .andExpect(jsonPath("$.name").value(name))
            .andExpect(jsonPath("$.description").value(description));
    }

    @Test
    @DisplayName("비공개 채널 생성")
    void createPrivateChannel_Success() throws Exception {
        // Given
        ChannelType channelType = ChannelType.PRIVATE;
        PrivateChannelCreateRequest createRequest = new PrivateChannelCreateRequest(
            List.of(savedUser.getId()));

        // When
        ResultActions result = mockMvc.perform(post("/api/channels/private")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createRequest)));

        // Then
        result.andExpect(status().isCreated())
            .andExpect(jsonPath("$.type").value(channelType.toString()))
            .andExpect(jsonPath("$.participants[0].id").value(savedUser.getId().toString()))
            .andExpect(jsonPath("$.participants[0].username").value(savedUser.getUsername()))
            .andExpect(jsonPath("$.participants[0].email").value(savedUser.getEmail()));
    }

    @Test
    @DisplayName("공개 채널 수정")
    void updatePublicChannel_Success() throws Exception {
        // Given
        String newName = "공개 채널 수정 테스트";
        String newDescription = "공개 채널 수정 테스트 채널입니다.";
        PublicChannelUpdateRequest updateRequest = new PublicChannelUpdateRequest(newName,
            newDescription);

        // When
        ResultActions result = mockMvc.perform(
            patch("/api/channels/{channelId}", savedPublicChannel.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)));

        // Then
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.type").value(ChannelType.PUBLIC.toString()))
            .andExpect(jsonPath("$.name").value(newName))
            .andExpect(jsonPath("$.description").value(newDescription));
    }

    @Test
    @DisplayName("채널 삭제")
    void deleteChannel_Success() throws Exception {
        // Given
        UUID channelId = savedPublicChannel.getId();

        // When
        ResultActions result = mockMvc.perform(delete("/api/channels/{channelId}", channelId));

        // Then
        result.andExpect(status().isNoContent());
        assertThat(channelRepository.findById(channelId).isPresent()).isFalse();
    }

    @Test
    @DisplayName("유저가 속한 채널 전체 조회")
    void findAll_Success() throws Exception {
        // Given
        UUID userId = savedUser.getId();

        // When
        ResultActions result = mockMvc.perform(
            get("/api/channels").param("userId", userId.toString()));

        // Then
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$[0].type").value(ChannelType.PUBLIC.toString()))
            .andExpect(jsonPath("$[0].name").value("공개채널테스트"))
            .andExpect(jsonPath("$[0].description").value("공개 채널 테스트입니다."))
            .andExpect(jsonPath("$[1].type").value(ChannelType.PRIVATE.toString()))
            .andExpect(jsonPath("$[1].participants[0].id").value(userId.toString()));
    }
}
