package com.sprint.mission.discodeit.service.jcf;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

class JCFChannelServiceTest {

    private JCFChannelService channelService;
    private JCFUserService userService;
    private User testOwner;
    private Channel testChannel;

    @BeforeEach
    public void setUp() {
        this.channelService = new JCFChannelService();
        this.userService = new JCFUserService();
        testOwner = userService.createUser("testOwner", "owner@test.com", "password");
        testChannel = channelService.createChannel("TestChannel", false, "", testOwner.getUserId());
    }

    @Test
    @DisplayName("채널 생성 - 정상 케이스")
    void createChannel_Success() {
        // Given
        String channelName = "NewChannel";
        boolean isPrivate = true;
        String password = "password123";

        // When
        Channel createdChannel = channelService.createChannel(channelName, isPrivate, password, testOwner.getUserId());

        // Then
        assertAll(
                () -> assertNotNull(createdChannel),
                () -> assertEquals(channelName, createdChannel.getChannelName()),
                () -> assertTrue(createdChannel.isPrivate()),
                () -> assertEquals(password, createdChannel.getPassword()),
                () -> assertEquals(testOwner.getUserId(), createdChannel.getOwnerChannelId())
        );
    }

    @Test
    @DisplayName("ID로 채널 조회 - 존재하는 채널")
    void getChannelById_ExistingChannel() {
        // Given - testChannel은 setUp()에서 생성됨
        UUID channelId = testChannel.getChannelId();

        // When
        Channel foundChannel = channelService.getChannelById(channelId);

        // Then
        assertAll(
                () -> assertNotNull(foundChannel),
                () -> assertEquals(testChannel.getChannelName(), foundChannel.getChannelName()),
                () -> assertEquals(testChannel.getOwnerChannelId(), foundChannel.getOwnerChannelId())
        );
    }

    @Test
    @DisplayName("모든 채널 조회")
    void getAllChannels() {
        // Given
        channelService.createChannel("Channel2", false, "", testOwner.getUserId());
        channelService.createChannel("Channel3", true, "pass", testOwner.getUserId());

        // When
        List<Channel> allChannels = channelService.getAllChannels();

        // Then
        assertAll(
                () -> assertNotNull(allChannels),
                () -> assertEquals(3, allChannels.size()) // testChannel + 2개 추가
        );
    }

    @Test
    @DisplayName("채널 참가 - 정상 케이스")
    void joinChannel_Success() {
        // Given
        User newUser = userService.createUser("participant", "participant@test.com", "pass");

        // When
        boolean joinResult = channelService.joinChannel(testChannel.getChannelId(), newUser.getUserId(), "");
        Set<UUID> participants = channelService.getChannelParticipants(testChannel.getChannelId());

        // Then
        assertAll(
                () -> assertTrue(joinResult),
                () -> assertTrue(participants.contains(newUser.getUserId()))
        );
    }

    @Test
    @DisplayName("채널 나가기 - 정상 케이스")
    void leaveChannel_Success() {
        // Given
        User participant = userService.createUser("participant", "participant@test.com", "pass");
        channelService.joinChannel(testChannel.getChannelId(), participant.getUserId(), "");

        // When
        boolean leaveResult = channelService.leaveChannel(testChannel.getChannelId(), participant.getUserId());
        Set<UUID> participants = channelService.getChannelParticipants(testChannel.getChannelId());
        // Then
        assertAll(
                () -> assertTrue(leaveResult),
                () -> assertFalse(participants.contains(participant.getUserId()))
        );
    }

    @Test
    @DisplayName("채널 정보 업데이트")
    void updateChannel_Success() {
        // Given
        String newName = "UpdatedChannel";
        boolean newPrivate = true;
        String newPassword = "newPass";

        // When
        channelService.updateChannel(testChannel.getChannelId(), newName, newPrivate, newPassword);
        Channel updatedChannel = channelService.getChannelById(testChannel.getChannelId());

        // Then
        assertAll(
                () -> assertNotNull(updatedChannel),
                () -> assertEquals(newName, updatedChannel.getChannelName()),
                () -> assertTrue(updatedChannel.isPrivate()),
                () -> assertEquals(newPassword, updatedChannel.getPassword())
        );
    }

    @Test
    @DisplayName("채널 삭제")
    void deleteChannel_Success() {
        // Given
        UUID channelId = testChannel.getChannelId();

        // When
        channelService.deleteChannel(channelId);

        // Then
        assertAll(
                () -> assertNull(channelService.getChannelById(channelId)),
                () -> assertTrue(channelService.getAllChannels().isEmpty())
        );
    }
}
