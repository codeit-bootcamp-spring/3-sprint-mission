package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.never;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@DisplayName("ChannelService 단위 테스트")
class BasicChannelServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ChannelMapper channelMapper;

    @Mock
    private ChannelRepository channelRepository;

    @Mock
    private ReadStatusRepository readStatusRepository;

    @Mock
    private MessageRepository messageRepository;

    @InjectMocks
    private BasicChannelService channelService;

    @Test
    @DisplayName("비공개 채널 생성 - 성공")
    void createPrivateChannel_Success() {
        // Given
        UUID user1Id = UUID.randomUUID();
        UUID user2Id = UUID.randomUUID();
        List<UUID> participantIds = List.of(user1Id, user2Id);

        User user1 = new User("user1", "1@email.com", "password123!", null);
        User user2 = new User("user2", "2@email.com", "password123!", null);
        List<User> participants = List.of(user1, user2);

        PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(participantIds);
        Channel privateChannel = new Channel(ChannelType.PRIVATE);
        ChannelDto privateChannelDto = new ChannelDto(UUID.randomUUID(),
            ChannelType.PRIVATE, null, null, null, Instant.now());

        given(channelRepository.save(any(Channel.class))).willReturn(privateChannel);
        given(userRepository.findAllById(participantIds)).willReturn(participants);
        given(readStatusRepository.saveAll(anyList())).willReturn(List.of());
        given(channelMapper.toDto(any(Channel.class))).willReturn(privateChannelDto);

        // When
        ChannelDto result = channelService.create(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.type()).isEqualTo(ChannelType.PRIVATE);
        assertThat(result.name()).isNull();

        then(channelRepository).should().save(any(Channel.class));
        then(userRepository).should().findAllById(participantIds);
        then(readStatusRepository).should().saveAll(anyList());
        then(channelMapper).should().toDto(any(Channel.class));

    }

    @Test
    @DisplayName("공개 채널 생성 - 성공")
    void createPublicChannel_Success() {
        // Given
        PublicChannelCreateRequest request = new PublicChannelCreateRequest("채널1", "테스트 채널입니다.");
        Channel publicChannel = new Channel(ChannelType.PUBLIC, request.name(),
            request.description());
        ChannelDto channelDto = new ChannelDto(UUID.randomUUID(), publicChannel.getType(),
            publicChannel.getName(), publicChannel.getDescription(), null, Instant.now());

        given(channelRepository.save(any(Channel.class))).willReturn(publicChannel);
        given(channelMapper.toDto(any(Channel.class))).willReturn(channelDto);

        // When
        ChannelDto result = channelService.create(request);

        // Then
        assertThat(result.type()).isEqualTo(ChannelType.PUBLIC);
        assertThat(result.name()).isEqualTo("채널1");
        assertThat(result.description()).isEqualTo("테스트 채널입니다.");

        then(channelRepository).should().save(any(Channel.class));
        then(channelMapper).should().toDto(any(Channel.class));
    }

    @Test
    @DisplayName("유저별 채널 조회 - 성공")
    void findAllByUserId_Success() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID channelId1 = UUID.randomUUID();
        UUID channelId2 = UUID.randomUUID();

        Channel channel1 = mock(Channel.class);
        Channel channel2 = mock(Channel.class);

        ReadStatus readStatus1 = new ReadStatus(null, channel1, Instant.now());
        ReadStatus readStatus2 = new ReadStatus(null, channel2, Instant.now());

        ChannelDto channelDto1 = mock(ChannelDto.class);
        ChannelDto channelDto2 = mock(ChannelDto.class);

        given(channel1.getId()).willReturn(channelId1);
        given(channel2.getId()).willReturn(channelId2);
        given(readStatusRepository.findAllByUserId(userId)).willReturn(
            List.of(readStatus1, readStatus2));
        given(channelRepository.findAllByTypeOrIdIn(eq(ChannelType.PUBLIC),
            eq(List.of(channelId1, channelId2))))
            .willReturn(List.of(channel1, channel2));
        given(channelMapper.toDto(channel1)).willReturn(channelDto1);
        given(channelMapper.toDto(channel2)).willReturn(channelDto2);

        // When
        List<ChannelDto> result = channelService.findAllByUserId(userId);

        // Then
        assertThat(result).containsExactly(channelDto1, channelDto2);
        then(readStatusRepository).should().findAllByUserId(userId);
        then(channelRepository).should()
            .findAllByTypeOrIdIn(eq(ChannelType.PUBLIC), eq(List.of(channelId1, channelId2)));
        then(channelMapper).should().toDto(channel1);
        then(channelMapper).should().toDto(channel2);
    }

    @Test
    @DisplayName("공개 채널 수정 - 성공")
    void update_Success() {
        UUID id = UUID.randomUUID();
        Channel existing = new Channel(ChannelType.PUBLIC, "old", "desc");
        ReflectionTestUtils.setField(existing, "id", id);

        PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("newName", "newDesc");

        ChannelDto expectedDto = new ChannelDto(id, ChannelType.PUBLIC, "newName", "newDesc", null,
            null);

        given(channelRepository.findById(id)).willReturn(Optional.of(existing));
        given(channelMapper.toDto(existing)).willReturn(expectedDto);

        // When
        ChannelDto result = channelService.update(id, request);

        // Then
        assertThat(result.name()).isEqualTo("newName");
        then(channelRepository).should().findById(id);
        then(channelMapper).should().toDto(existing);
    }

    @Test
    @DisplayName("공개 채널 수정 - 실패 (private 채널 수정 시도)")
    void update_Fail() {
        UUID id = UUID.randomUUID();
        Channel privateChannel = new Channel(ChannelType.PRIVATE);
        ReflectionTestUtils.setField(privateChannel, "id", id);

        given(channelRepository.findById(id)).willReturn(Optional.of(privateChannel));

        assertThatThrownBy(
            () -> channelService.update(id, new PublicChannelUpdateRequest("newName", "newDesc")))
            .isInstanceOf(PrivateChannelUpdateException.class);

        then(channelRepository).should().findById(id);
    }

    @Test
    @DisplayName("채널 삭제 - 성공")
    void delete_Success() {
        UUID id = UUID.randomUUID();
        given(channelRepository.existsById(id)).willReturn(true);

        // When
        channelService.delete(id);

        // Then
        then(channelRepository).should().existsById(id);
        then(messageRepository).should().deleteAllByChannelId(id);
        then(readStatusRepository).should().deleteAllByChannelId(id);
        then(channelRepository).should().deleteById(id);
    }

    @Test
    @DisplayName("채널 삭제 - 실패")
    void delete_Fail() {
        UUID id = UUID.randomUUID();
        given(channelRepository.existsById(id)).willReturn(false);

        assertThatThrownBy(() -> channelService.delete(id))
            .isInstanceOf(ChannelNotFoundException.class);

        then(channelRepository).should().existsById(id);
        then(messageRepository).should(never()).deleteAllByChannelId(any());
        then(readStatusRepository).should(never()).deleteAllByChannelId(any());
        then(channelRepository).should(never()).deleteById(any());
    }
}