package com.sprint.mission.discodeit.service.jcf;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;

class JCFChannelServiceTest {

    private JCFUserRepository userRepository;
    private JCFUserService userService;
    private JCFChannelRepository channelRepository;
    private JCFChannelService channelService;
    private User testOwner;
    private Channel testChannel;

    @BeforeEach
    public void setUp() {
        JCFUserRepository.clearInstance();
        JCFChannelRepository.clearInstance();
        
        this.userRepository = JCFUserRepository.getInstance();
        this.userService = JCFUserService.getInstance(this.userRepository);
        this.channelRepository = JCFChannelRepository.getInstance();
        this.channelService = JCFChannelService.getInstance(this.userService, this.channelRepository);
        
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

    @Test
    @DisplayName("채널 생성 시 존재하지 않는 소유자 ID 사용 시 예외 발생")
    void createChannel_shouldThrowExceptionForNonExistingOwner() {
        // Given
        UUID nonExistingOwnerId = UUID.randomUUID();

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            channelService.createChannel("FailChannel", false, "", nonExistingOwnerId);
        });
    }

    @Test
    @DisplayName("채널 참가 시 존재하지 않는 사용자 ID 사용 시 예외 발생")
    void joinChannel_shouldThrowExceptionForNonExistingUser() {
        // Given
        UUID nonExistingUserId = UUID.randomUUID();

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            channelService.joinChannel(testChannel.getChannelId(), nonExistingUserId, "");
        });
    }

    @Test
    @DisplayName("채널 참가 시 존재하지 않는 채널 ID 사용 시 예외 발생")
    void joinChannel_shouldThrowExceptionForNonExistingChannel() {
        // Given
        User existingUser = userService.createUser("existingUser", "exist@test.com", "pass");
        UUID nonExistingChannelId = UUID.randomUUID();

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            channelService.joinChannel(nonExistingChannelId, existingUser.getUserId(), "");
        });
    }

    @Test
    @DisplayName("비공개 채널 참가 시 잘못된 비밀번호 사용 시 예외 발생")
    void joinChannel_shouldThrowExceptionForWrongPasswordInPrivateChannel() {
        // Given
        User userTryingToJoin = userService.createUser("joiner", "joiner@test.com", "pass");
        Channel privateChannel = channelService.createChannel("PrivateRoom", true, "secret", testOwner.getUserId());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            channelService.joinChannel(privateChannel.getChannelId(), userTryingToJoin.getUserId(), "wrongPassword");
        });
    }

    @Test
    @DisplayName("이미 참가한 채널에 다시 참가 시도 시 false 반환")
    void joinChannel_shouldReturnFalseWhenJoiningAlreadyJoinedChannel() {
        // Given: setUp에서 testOwner는 이미 채널 생성 시 참가됨

        // When
        boolean result = channelService.joinChannel(testChannel.getChannelId(), testOwner.getUserId(), "");

        // Then
        assertFalse(result, "이미 참가한 사용자가 다시 참가를 시도하면 false를 반환해야 합니다.");
    }

    @Test
    @DisplayName("채널 소유자가 채널 나가기 시도 시 예외 발생")
    void leaveChannel_shouldThrowExceptionWhenOwnerTriesToLeave() {
        // Given: testOwner는 testChannel의 소유자

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            channelService.leaveChannel(testChannel.getChannelId(), testOwner.getUserId());
        });
    }

    @Test
    @DisplayName("존재하지 않는 채널에서 나가기 시도 시 예외 발생")
    void leaveChannel_shouldThrowExceptionForNonExistingChannel() {
        // Given
        User participant = userService.createUser("leaver", "leaver@test.com", "pass");
        UUID nonExistingChannelId = UUID.randomUUID();

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            channelService.leaveChannel(nonExistingChannelId, participant.getUserId());
        });
    }

    @Test
    @DisplayName("참가하지 않은 채널에서 나가기 시도 시 false 반환")
    void leaveChannel_shouldReturnFalseWhenLeavingNotJoinedChannel() {
        // Given
        User nonParticipant = userService.createUser("nonParticipant", "non@test.com", "pass");

        // When
        boolean result = channelService.leaveChannel(testChannel.getChannelId(), nonParticipant.getUserId());

        // Then
        assertFalse(result, "참가하지 않은 사용자가 나가기를 시도하면 false를 반환해야 합니다.");
    }

    @Test
    @DisplayName("존재하지 않는 채널의 참가자 조회 시 예외 발생")
    void getChannelParticipants_shouldThrowExceptionForNonExistingChannel() {
        // Given
        UUID nonExistingChannelId = UUID.randomUUID();

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            channelService.getChannelParticipants(nonExistingChannelId);
        });
    }
}
