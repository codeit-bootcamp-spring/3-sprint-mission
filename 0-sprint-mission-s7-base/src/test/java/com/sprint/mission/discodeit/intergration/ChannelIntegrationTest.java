package com.sprint.mission.discodeit.intergration;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.hamcrest.Matchers;
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
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("채널 통합 테스트")
public class ChannelIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserStatusRepository userStatusRepository;

    @Test
    @Transactional
    @DisplayName("Public 채널 생성 - case : success")
    void createPublicChannelSuccess() throws Exception {
        PublicChannelCreateRequest request = new PublicChannelCreateRequest("testPublicChannel","testPublicChannel description");
        mockMvc.perform(post("/api/channels/public")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("testPublicChannel"));
    }

    @Test
    @Transactional
    @DisplayName("Public 채널 생성 - case : 잘못된 채널 이름으로 인한 failed")
    void createPublicChannelFail() throws Exception {
        PublicChannelCreateRequest request = new PublicChannelCreateRequest("","testPublicChannel description");

        mockMvc.perform(post("/api/channels/public")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    @DisplayName("Private 채널 생성 - case : success")
    void createPrivateChannelSuccess() throws Exception {
        User user1 = userRepository.save(new User("테스트맨1","test1@test.com","1111",null));
        User user2 = userRepository.save(new User("테스트맨2","test2@test.com","2222",null));
        UserStatus userStatus1 = userStatusRepository.save(new UserStatus(user1, Instant.now()));
        UserStatus userStatus2 = userStatusRepository.save(new UserStatus(user2, Instant.now()));

        PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(
            List.of(user1.getId(),user2.getId()));

        mockMvc.perform(post("/api/channels/private")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.type").value(ChannelType.PRIVATE.name()))
            .andExpect(jsonPath("$.participants[*].username", Matchers.containsInAnyOrder("테스트맨1","테스트맨2")));

    }

    @Test
    @Transactional
    @DisplayName("Public 채널 수정 - case : success")
    void updatePublicChannelSuccess() throws Exception {
        Channel channel = channelRepository.save(new Channel(ChannelType.PUBLIC,"testChannel",null));
        UUID channelId = channel.getId();
        PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("testPublicChannel","testPublicChannel description");

        mockMvc.perform(patch("/api/channels/{channelId}",channelId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.description").value("testPublicChannel description"));
    }

    @Test
    @Transactional
    @DisplayName("Private 채널 삭제 - case : success")
    void deletePrivateChannelSuccess() throws Exception {
        Channel channel = channelRepository.save(new Channel(ChannelType.PRIVATE,null,null));
        UUID channelId = channel.getId();

        mockMvc.perform(delete("/api/channels/{channelId}",channelId))
            .andExpect(status().isNoContent());
    }
}
