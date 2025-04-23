package com.sprint.mission.discodeit.service.basic;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;

class BasicChannelServiceTest {

    private ChannelService channelService;
    private JCFUserRepository userRepository;
    private JCFChannelRepository channelRepository;
    private User testOwner;
    private Channel testChannel;

    @BeforeEach
    void setUp() {
        JCFUserRepository.clearInstance();
        JCFChannelRepository.clearInstance();
        userRepository = JCFUserRepository.getInstance();
        channelRepository = JCFChannelRepository.getInstance();
        userRepository.clearData();
        channelRepository.clearData();
        channelService = new BasicChannelService(userRepository, channelRepository);
        testOwner = userRepository.save(new User("testOwner", "owner@test.com", "password"));
        testChannel = channelService.createChannel("TestChannel", false, "", testOwner.getUserId());
    }

    @Test
    @DisplayName("채널 생성 - 정상 케이스")
    void createChannel_Success() {
        Channel createdChannel = channelService.createChannel("NewChannel", true, "password123", testOwner.getUserId());
        assertAll(
                () -> assertNotNull(createdChannel),
                () -> assertEquals("NewChannel", createdChannel.getChannelName()),
                () -> assertTrue(createdChannel.isPrivate()),
                () -> assertEquals("password123", createdChannel.getPassword()),
                () -> assertEquals(testOwner.getUserId(), createdChannel.getOwnerChannelId())
        );
    }

    @Test
    @DisplayName("ID로 채널 조회 - 존재하는 채널")
    void getChannelById_ExistingChannel() {
        Channel foundChannel = channelService.getChannelById(testChannel.getChannelId());
        assertAll(
                () -> assertNotNull(foundChannel),
                () -> assertEquals(testChannel.getChannelName(), foundChannel.getChannelName()),
                () -> assertEquals(testChannel.getOwnerChannelId(), foundChannel.getOwnerChannelId())
        );
    }

    @Test
    @DisplayName("모든 채널 조회")
    void getAllChannels() {
        channelService.createChannel("Channel2", false, "", testOwner.getUserId());
        channelService.createChannel("Channel3", true, "pass", testOwner.getUserId());
        List<Channel> allChannels = channelService.getAllChannels();
        assertAll(
                () -> assertNotNull(allChannels),
                () -> assertEquals(3, allChannels.size())
        );
    }

    @Test
    @DisplayName("채널 참가 - 정상 케이스")
    void joinChannel_Success() {
        User newUser = userRepository.save(new User("participant", "participant@test.com", "pass"));
        boolean joinResult = channelService.joinChannel(testChannel.getChannelId(), newUser.getUserId(), "");
        Set<UUID> participants = channelService.getChannelParticipants(testChannel.getChannelId());
        assertAll(
                () -> assertTrue(joinResult),
                () -> assertTrue(participants.contains(newUser.getUserId()))
        );
    }

    @Test
    @DisplayName("채널 삭제")
    void deleteChannel_Success() {
        UUID channelId = testChannel.getChannelId();
        channelService.deleteChannel(channelId);
        assertNull(channelService.getChannelById(channelId));
    }
}
