package com.sprint.mission.discodeit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;
import static org.mockito.BDDMockito.then;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ReadStatusResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusAlreadyExistsException;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicReadStatusService;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class BasicReadStatusServiceTest {

    @InjectMocks
    private BasicReadStatusService service;

    @Mock
    private ReadStatusRepository readStatusRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ChannelRepository channelRepository;
    @Mock
    private ReadStatusMapper readStatusMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("읽음 상태 생성 성공")
    void shouldCreateReadStatusSuccessfully() {
        UUID userId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        Instant lastReadAt = Instant.now();
        ReadStatusCreateRequest request = new ReadStatusCreateRequest(userId, channelId,
            lastReadAt);

        User user = new User("user", "user@email.com", "pw", null);
        Channel channel = new Channel(ChannelType.PUBLIC, "channel name", "channel description");
        ReadStatus readStatus = new ReadStatus(user, channel, lastReadAt);
        ReadStatusResponse response = mock(ReadStatusResponse.class);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
        given(readStatusRepository.existsByUserIdAndChannelId(userId, channelId)).willReturn(false);
        given(readStatusMapper.toResponse(any())).willReturn(response);

        ReadStatusResponse result = service.create(request);
        assertThat(result).isEqualTo(response);
    }

    @Test
    @DisplayName("사용자 미발견시 읽음 상태 생성 실패")
    void shouldThrowUserNotFoundException_whenUserNotFoundForCreate() {
        UUID userId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        ReadStatusCreateRequest request = new ReadStatusCreateRequest(userId, channelId,
            Instant.now());

        given(userRepository.findById(userId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(request)).isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("채널 미발견시 읽음 상태 생성 실패")
    void shouldThrowChannelNotFoundException_whenChannelNotFoundForCreate() {
        UUID userId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        ReadStatusCreateRequest request = new ReadStatusCreateRequest(userId, channelId,
            Instant.now());

        given(userRepository.findById(userId)).willReturn(Optional.of(mock(User.class)));
        given(channelRepository.findById(channelId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(request)).isInstanceOf(
            ChannelNotFoundException.class);
    }

    @Test
    @DisplayName("중복 읽음 상태 생성 시 예외 발생")
    void shouldThrowReadStatusAlreadyExistsException_whenDuplicateReadStatusForCreate() {
        UUID userId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        ReadStatusCreateRequest request = new ReadStatusCreateRequest(userId, channelId,
            Instant.now());

        given(userRepository.findById(userId)).willReturn(Optional.of(mock(User.class)));
        given(channelRepository.findById(channelId)).willReturn(Optional.of(mock(Channel.class)));
        given(readStatusRepository.existsByUserIdAndChannelId(userId, channelId)).willReturn(true);

        assertThatThrownBy(() -> service.create(request)).isInstanceOf(
            ReadStatusAlreadyExistsException.class);
    }

    @Test
    @DisplayName("읽음 상태 조회 성공")
    void shouldFindReadStatusSuccessfully() {
        UUID id = UUID.randomUUID();
        ReadStatus readStatus = mock(ReadStatus.class);
        ReadStatusResponse response = mock(ReadStatusResponse.class);

        given(readStatusRepository.findById(id)).willReturn(Optional.of(readStatus));
        given(readStatusMapper.toResponse(readStatus)).willReturn(response);

        ReadStatusResponse result = service.find(id);
        assertThat(result).isEqualTo(response);
    }

    @Test
    @DisplayName("존재하지 않는 읽음 상태 조회 시 예외 발생")
    void shouldThrowReadStatusNotFoundException_whenReadStatusNotFoundForFind() {
        UUID id = UUID.randomUUID();
        given(readStatusRepository.findById(id)).willReturn(Optional.empty());
        assertThatThrownBy(() -> service.find(id)).isInstanceOf(ReadStatusNotFoundException.class);
    }

    @Test
    @DisplayName("사용자 ID로 읽음 상태 목록 조회 성공")
    void shouldFindAllByUserIdSuccessfully() {
        UUID userId = UUID.randomUUID();
        List<ReadStatus> list = List.of(mock(ReadStatus.class), mock(ReadStatus.class));
        List<ReadStatusResponse> responseList = List.of(mock(ReadStatusResponse.class),
            mock(ReadStatusResponse.class));

        given(readStatusRepository.findAllByUserId(userId)).willReturn(list);
        given(readStatusMapper.toResponse(any())).willReturn(responseList.get(0),
            responseList.get(1));

        List<ReadStatusResponse> result = service.findAllByUserId(userId);
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("읽음 상태 수정 성공")
    void shouldUpdateReadStatusSuccessfully() {
        UUID id = UUID.randomUUID();
        Instant updatedTime = Instant.now();
        ReadStatusUpdateRequest request = new ReadStatusUpdateRequest(updatedTime);
        ReadStatus readStatus = mock(ReadStatus.class);
        ReadStatusResponse response = mock(ReadStatusResponse.class);

        given(readStatusRepository.findById(id)).willReturn(Optional.of(readStatus));
        given(readStatusMapper.toResponse(readStatus)).willReturn(response);

        ReadStatusResponse result = service.update(id, request);
        assertThat(result).isEqualTo(response);
    }

    @Test
    @DisplayName("존재하지 않는 읽음 상태 수정 시 예외 발생")
    void shouldThrowReadStatusNotFoundException_whenReadStatusNotFoundForUpdate() {
        UUID id = UUID.randomUUID();
        ReadStatusUpdateRequest request = new ReadStatusUpdateRequest(Instant.now());

        given(readStatusRepository.findById(id)).willReturn(Optional.empty());
        assertThatThrownBy(() -> service.update(id, request)).isInstanceOf(
            ReadStatusNotFoundException.class);
    }

    @Test
    @DisplayName("읽음 상태 삭제 성공")
    void shouldDeleteReadStatusSuccessfully() {
        UUID id = UUID.randomUUID();
        given(readStatusRepository.existsById(id)).willReturn(true);

        service.delete(id);
        then(readStatusRepository).should().deleteById(id);
    }

    @Test
    @DisplayName("존재하지 않는 읽음 상태 삭제 시 예외 발생")
    void shouldThrowNoSuchElementException_whenReadStatusNotFoundForDelete() {
        UUID id = UUID.randomUUID();
        given(readStatusRepository.existsById(id)).willReturn(false);

        assertThatThrownBy(() -> service.delete(id)).isInstanceOf(NoSuchElementException.class);
    }
}
