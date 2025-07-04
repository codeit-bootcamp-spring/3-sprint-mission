package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("채널 서비스 통합 테스트")
@Transactional
public class ChannelServiceIntegrationTest {

    @Autowired private ChannelService channelService;
    @Autowired private UserRepository userRepository;
    @Autowired private ChannelRepository channelRepository;
    @Autowired private ReadStatusRepository readStatusRepository;
    @Autowired private UserStatusRepository userStatusRepository;
    @Autowired private BinaryContentRepository binaryContentRepository;
    @Autowired private BinaryContentStorage binaryContentStorage;
    @Autowired private MessageRepository messageRepository;
    @Autowired private MockMvc mockMvc;

    @Test
    @DisplayName("PUBLIC 채널 생성 요청 시 모든 계층에서 올바르게 동작해야 한다")
    void createPublicChannelProcessIntegration() throws Exception {
        // given
        String json = """
            {
                "name": "test-channel",
                "description": "테스트 설명"
            }
            """;

        // when & then
        mockMvc.perform(post("/api/channels/public")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("test-channel"))
                .andExpect(jsonPath("$.type").value("PUBLIC"));

        List<Channel> found = channelRepository.findAll();

        Channel savedChannel = found.get(0);
        assertThat(savedChannel.getName()).isEqualTo("test-channel");
        assertThat(savedChannel.getDescription()).isEqualTo("테스트 설명");
        assertThat(savedChannel.getType()).isEqualTo(ChannelType.PUBLIC);
    }

    /*
    * PRIVATE 채널 생성 시 모든 계층에서 정상동작 하는지 검증한다
    * 채널 생성, 참가한 유저들의 ReadStatus 생성 까지의
    * 모든 비즈니스 플로우를 데이터베이스와 함께 검증한다
    * */
    @Test
    @DisplayName("PRIVATE 채널 생성 요청 시 모든 계층에서 올바르게 동작해야 한다")
    void createPrivateChannelProcessIntegration() throws Exception {
        // given
        BinaryContent binaryContent = new BinaryContent("test", 100L, "png");
        binaryContentRepository.save(binaryContent);
        binaryContentRepository.flush();

        binaryContentStorage.put(binaryContent.getId(), "test File".getBytes());

        BinaryContent findBinaryContent = binaryContentRepository.findById(binaryContent.getId()).orElseThrow();

        User user = userRepository.save(new User("test", "test@gmail.com", "test1234", findBinaryContent));
        UserStatus userStatus = userStatusRepository.save(new UserStatus(user, Instant.now()));
        user.setUserStatus(userStatus);

        userRepository.flush();
        userStatusRepository.flush();

        String json = """
        {
            "participantIds": ["%s"]
        }
        """.formatted(user.getId());

        // when & then
        mockMvc.perform(post("/api/channels/private")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value("PRIVATE"))
                .andExpect(jsonPath("$.participants[0].id").value(user.getId().toString()));

        List<Channel> channels = channelRepository.findAll();
        assertThat(channels).hasSize(1);

        List<ReadStatus> statuses = readStatusRepository.findAllByChannelId(channels.get(0).getId());
        assertThat(statuses).hasSize(1);
        assertThat(statuses.get(0).getUser().getId()).isEqualTo(user.getId());
    }

    @Test
    @DisplayName("PUBLIC 채널 수정 요청 시 모든 계층에서 올바르게 동작해야 한다")
    void updatePublicChannelProcessIntegration() throws Exception {
        // given: 채널 생성
        ChannelDto channelDto = channelService.create(new PublicChannelCreateRequest("초기명", "초기설명"));

        String json = """
        {
            "newName": "수정",
            "newDescription": "수정"
        }
        """;

        // when & then
        mockMvc.perform(put("/api/channels/" + channelDto.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("수정"))
                .andExpect(jsonPath("$.id").value(channelDto.id().toString()));

        Channel updated = channelRepository.findById(channelDto.id()).orElseThrow();
        assertThat(updated.getName()).isEqualTo("수정");
    }

    /*
    * 채널 삭제 시 모든 메세지를 삭제해아 한다
    * */
    @Test
    @DisplayName("채널 삭제 요청 시 모든 계층에서 올바르게 동작해야 한다")
    void deleteChannelProcessIntegration() throws Exception {
        // given
        BinaryContent binaryContent = new BinaryContent("test", 100L, "png");
        binaryContentRepository.saveAndFlush(binaryContent);

        binaryContentStorage.put(binaryContent.getId(), "test File".getBytes());

        User user = userRepository.save(new User("test", "test@gmail.com", "test1234", binaryContent));
        UserStatus userStatus = userStatusRepository.save(new UserStatus(user, Instant.now()));
        user.setUserStatus(userStatus);
        userRepository.flush();
        userStatusRepository.flush();

        Channel channel = channelRepository.saveAndFlush(new Channel(ChannelType.PUBLIC, "test", "test"));

        messageRepository.saveAll(List.of(
                new Message("test1", channel, user),
                new Message("test2", channel, user),
                new Message("test3", channel, user)
        ));
        messageRepository.flush();

        // when
        mockMvc.perform(delete("/api/channels/{channelId}", channel.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        // then
        assertThat(channelRepository.findById(channel.getId())).isEmpty();
        assertThat(messageRepository.findAllByChannelId(channel.getId())).isEmpty();
    }
}
