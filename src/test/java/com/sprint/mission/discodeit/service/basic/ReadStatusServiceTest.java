package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReadStatusServiceTest {

    @Mock
    private ReadStatusRepository readStatusRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ChannelRepository channelRepository;

    @Mock
    private ReadStatusMapper readStatusMapper;

    @InjectMocks
    private BasicReadStatusService readStatusService;

    @Test
    @DisplayName("읽기 상태 생성 - 성공")
    void create_Success() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        Instant lastReadAt = Instant.now();
        ReadStatusCreateRequest request = new ReadStatusCreateRequest(userId, channelId,
            lastReadAt);

        User user = mock(User.class);
        Channel channel = mock(Channel.class);
        given(user.getId()).willReturn(userId);
        given(channel.getId()).willReturn(channelId);

        ReadStatus savedReadStatus = new ReadStatus(user, channel, lastReadAt);
        ReadStatusDto expectedDto = new ReadStatusDto(UUID.randomUUID(), null, null, lastReadAt);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
        given(readStatusRepository.existsByUserIdAndChannelId(userId, channelId)).willReturn(false);
        given(readStatusRepository.save(any(ReadStatus.class))).willReturn(savedReadStatus);
        given(readStatusMapper.toDto(any(ReadStatus.class))).willReturn(expectedDto);

        // When
        ReadStatusDto result = readStatusService.create(request);

        // Then
        assertThat(result).isEqualTo(expectedDto);
        then(userRepository).should().findById(userId);
        then(channelRepository).should().findById(channelId);
        then(readStatusRepository).should().existsByUserIdAndChannelId(userId, channelId);
        then(readStatusRepository).should().save(any(ReadStatus.class));
    }

    @Test
    @DisplayName("읽기 상태 생성 - 사용자를 찾을 수 없어 실패")
    void create_FailWhenUserNotFound() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        ReadStatusCreateRequest request = new ReadStatusCreateRequest(userId, channelId,
            Instant.now());

        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> readStatusService.create(request))
            .isInstanceOf(UserNotFoundException.class);

        then(userRepository).should().findById(userId);
        then(channelRepository).should(never()).findById(channelId);
        then(readStatusRepository).should(never()).save(any());
    }

    @Test
    @DisplayName("읽기 상태 생성 - 채널을 찾을 수 없어 실패")
    void create_FailWhenChannelNotFound() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        ReadStatusCreateRequest request = new ReadStatusCreateRequest(userId, channelId,
            Instant.now());

        User user = new User("testuser", "test@example.com", "password", null);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(channelRepository.findById(channelId)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> readStatusService.create(request))
            .isInstanceOf(ChannelNotFoundException.class);

        then(userRepository).should().findById(userId);
        then(channelRepository).should().findById(channelId);
        then(readStatusRepository).should(never()).save(any());
    }

    @Test
    @DisplayName("읽기 상태 생성 - 이미 존재할 때 실패")
    void create_FailWhenReadStatusAlreadyExists() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        ReadStatusCreateRequest request = new ReadStatusCreateRequest(userId, channelId,
            Instant.now());

        User user = mock(User.class);
        Channel channel = mock(Channel.class);
        given(user.getId()).willReturn(userId);
        given(channel.getId()).willReturn(channelId);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
        given(readStatusRepository.existsByUserIdAndChannelId(userId, channelId)).willReturn(true);

        // When & Then
        assertThatThrownBy(() -> readStatusService.create(request))
            .isInstanceOf(ReadStatusNotFoundException.class);

        then(userRepository).should().findById(userId);
        then(channelRepository).should().findById(channelId);
        then(readStatusRepository).should().existsByUserIdAndChannelId(userId, channelId);
        then(readStatusRepository).should(never()).save(any());
    }

}