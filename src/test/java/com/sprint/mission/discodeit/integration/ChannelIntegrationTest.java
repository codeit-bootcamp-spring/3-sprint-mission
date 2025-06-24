package com.sprint.mission.discodeit.integration;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sprint.mission.discodeit.controller.ChannelController;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@SpringBootTest
@DisplayName("채널 통합 테스트")
@Transactional
public class ChannelIntegrationTest {

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private ChannelService channelService;

    @Autowired
    private ChannelController channelController;

    @Autowired
    private ChannelMapper channelMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReadStatusRepository readStatusRepository;

    @Test
    @DisplayName("공개채널을 생성할 수 있어야 한다")
    void createPublicChannel_Success() {
        //given
        PublicChannelCreateRequest request = new PublicChannelCreateRequest("Test Channel",
            "Test Channel Description");

        //when
        ChannelDto result = channelController.create(request).getBody();

        //then
        assertThat(result).isNotNull();
        assertThat(result.type()).isEqualTo(ChannelType.PUBLIC);
        assertThat(result.name()).isEqualTo("Test Channel");
        assertThat(result.description()).isEqualTo("Test Channel Description");
        assertThat(result.id()).isNotNull();

        // Database 검증
        assertThat(channelRepository.count()).isEqualTo(1);
        Channel savedChannel = channelRepository.findAll().get(0);
        assertThat(savedChannel.getName()).isEqualTo("Test Channel");
        assertThat(savedChannel.getDescription()).isEqualTo("Test Channel Description");
        assertThat(savedChannel.getType()).isEqualTo(ChannelType.PUBLIC);
    }

    @Test
    @DisplayName("비공개채널을 생성할 수 있어야 한다")
    void createPrivateChannel_Success() {
        //given
        User user1 = new User("user1", "EMAIL1", "password1", null);
        User user2 = new User("user2", "EMAIL2", "password2", null);
        UserStatus status1 = new UserStatus(user1, Instant.now());
        UserStatus status2 = new UserStatus(user2, Instant.now());

        userRepository.save(user1);
        userRepository.save(user2);
        PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(Arrays.asList(
            user1.getId(), user2.getId()));

        //when
        ChannelDto result = channelController.create(request).getBody();

        //then
        assertThat(result).isNotNull();
        assertThat(result.type()).isEqualTo(ChannelType.PRIVATE);

        // Database 검증
        assertThat(channelRepository.count()).isEqualTo(1);
        Channel savedChannel = channelRepository.findAll().get(0);
        assertThat(savedChannel.getType()).isEqualTo(ChannelType.PRIVATE);

        assertThat(readStatusRepository.count()).isEqualTo(2);
        List<ReadStatus> readStatuses = readStatusRepository.findAllByChannelIdWithUser(
            savedChannel.getId());
        assertThat(readStatuses).hasSize(2);
        List<UUID> actualParticipantIds = readStatuses.stream()
            .map(rs -> rs.getUser().getId())
            .toList();
        assertThat(actualParticipantIds).containsExactlyInAnyOrderElementsOf(Arrays.asList(
            user1.getId(), user2.getId()));
    }

    @Test
    @DisplayName("비공개 채널 생성할때 존재하지 않는 사용자를 포함하면 생성되지 않아야 한다")
    @Transactional
    void createPrivateChannel_NonExistentUser_Fail() {
        // Given - 일부 사용자만 생성
        User user1 = new User("user1", "user1@example.com", "password1", null);
        UserStatus userStatus = new UserStatus(user1, Instant.now());
        userRepository.save(user1);

        UUID nonExistentUserId = UUID.randomUUID();
        List<UUID> participantIds = Arrays.asList(user1.getId(), nonExistentUserId);
        PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(participantIds);

        // Database 검증 - 채널이 생성되지 않았는지 확인
        assertThat(channelRepository.count()).isEqualTo(0);
        assertThat(readStatusRepository.count()).isEqualTo(0);
    }

    @Test
    @DisplayName("채널 삭제를 할 수 있어야 한다")
    @Transactional
    void deleteChannel_Success() {
        // Given - 테스트 채널 생성
        Channel channel = new Channel(ChannelType.PUBLIC, "test-channel", "test description");
        channelRepository.save(channel);
        UUID channelId = channel.getId();

        // When
        channelController.delete(channelId);

        // Then - Database 검증
        assertThat(channelRepository.count()).isEqualTo(0);
        assertThat(channelRepository.findById(channelId)).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 채널을 삭제 시도시 예외가 발생해야 한다")
    @Transactional
    void deleteChannel_NotFound_Fail() {
        // Given - 존재하지 않는 채널 ID
        UUID nonExistentChannelId = UUID.randomUUID();

        // When & Then
        assertThatThrownBy(() -> channelService.delete(nonExistentChannelId))
            .isInstanceOf(ChannelNotFoundException.class);

        // Database 검증
        assertThat(channelRepository.count()).isEqualTo(0);
    }
}


