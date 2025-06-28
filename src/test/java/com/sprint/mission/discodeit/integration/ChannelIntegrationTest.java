package com.sprint.mission.discodeit.integration;

import static org.assertj.core.api.Assertions.assertThat;
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
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;
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
    private UserService userService;

    private Channel savedPublicChannel;
    private Channel savedPrivateChannel;
    private User savedUser;

    @BeforeEach
    void setUp() {
        Channel publicChannel = new Channel(ChannelType.PUBLIC, "공개채널테스트", "공개 채널 테스트입니다.");
        savedPublicChannel = channelRepository.save(publicChannel);

        User user = new User("테스트유저", "test@codeit.com", "test1234", null, null);
        savedUser = userRepository.save(user);

        // 유저가 참여한 개인채널 생성
        PrivateChannelCreateRequest createRequest = new PrivateChannelCreateRequest(
                List.of(savedUser.getId()));
        ChannelDto savedPrivateChannelDto = channelService.create(createRequest);
        savedPrivateChannel = channelRepository.findById(savedPrivateChannelDto.id()).orElse(null);
    }

    @Test
    @DisplayName("공개 채널 생성 요청 시 201 응답과 함꼐 생성된 채널 정보를 반환한다.")
    void createPublicChannel_Success() throws Exception {

        // given
        String name = "공개 채널 생성 테스트";
        String description = "공개 채널 생성 테스트 채널입니다.";

        PublicChannelCreateRequest createRequest = new PublicChannelCreateRequest(name,
                description);

        // when & then - controller단에서 수행 후 올바른 요청이 오는지 검증
        mockMvc.perform(post("/api/channels/public")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value(ChannelType.PUBLIC.toString()))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.description").value(description));
    }

    @Test
    @DisplayName("개인 채널 생성 요청 시 201 응답과 함께 생성된 채널 정보를 반환한다.")
    void createPrivateChannel_Success() throws Exception {

        // given
        ChannelType channelType = ChannelType.PRIVATE;
        PrivateChannelCreateRequest createRequest = new PrivateChannelCreateRequest(
                List.of(savedUser.getId()));

        // when & then
        mockMvc.perform(post("/api/channels/private")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value(channelType.toString()))
                .andExpect(jsonPath("$.participants[0].id").value(savedUser.getId().toString()))
                .andExpect(jsonPath("$.participants[0].username").value(savedUser.getUsername()))
                .andExpect(jsonPath("$.participants[0].email").value(savedUser.getEmail()));
    }

    @Test
    @DisplayName("공개 채널 수정 요청 시 200 응답과 함께 수정된 채널 정보를 반환한다.")
    void updatePublicChannel_Success() throws Exception {

        // given
        String newName = "공개 채널 수정 테스트";
        String newDescription = "공개 채널 수정 테스트 채널입니다.";

        PublicChannelUpdateRequest updateRequest = new PublicChannelUpdateRequest(newName,
                newDescription);

        // when & then
        mockMvc.perform(patch("/api/channels/{channelId}", savedPublicChannel.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value(ChannelType.PUBLIC.toString()))
                .andExpect(jsonPath("$.name").value(newName))
                .andExpect(jsonPath("$.description").value(newDescription));
    }

    @Test
    @DisplayName("채널 삭제 요청 시 204 응답과 함께 채널이 삭제된다.")
    void deleteChannel_Success() throws Exception {

        // given
        UUID channelId = savedPublicChannel.getId();

        // when & then
        mockMvc.perform(delete("/api/channels/{channelId}", channelId))
                .andExpect(status().isNoContent());
        assertThat(channelRepository.findById(channelId).isPresent()).isFalse();
    }

    @Test
    @DisplayName("특정 사용자가 조회 가능한 전체 채널 조회 요청 시 200 응답과 함께 조회 가능한 채널 목록이 반환된다.")
    void findAll_Success() throws Exception {

        // given
        UUID userId = savedUser.getId();

        // when & then
        mockMvc.perform(get("/api/channels").param("userId", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value(ChannelType.PUBLIC.toString()))
                .andExpect(jsonPath("$[0].name").value("공개채널테스트"))
                .andExpect(jsonPath("$[0].description").value("공개 채널 테스트입니다."))
                .andExpect(jsonPath("$[1].type").value(ChannelType.PRIVATE.toString()))
                .andExpect(jsonPath("$[1].participants[0].id").value(userId.toString()));
    }
}
