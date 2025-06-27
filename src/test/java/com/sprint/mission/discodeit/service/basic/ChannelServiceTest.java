package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
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
public class ChannelServiceTest {

    @Mock
    private ChannelRepository channelRepository;

    @Mock
    private ReadStatusRepository readStatusRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ChannelMapper channelMapper;

    @InjectMocks
    private BasicChannelService channelService;

    @Test
    @DisplayName("공개 채널 생성 - 성공")
    void createPublicChannel_Success() {
        //Given
        String name = "testChannel";
        String description = "testDescription";
        PublicChannelCreateRequest request = new PublicChannelCreateRequest(name, description);
        List<UserDto> users = List.of();

        Channel savedChannel = new Channel(ChannelType.PUBLIC, name, description);
        ChannelDto expectedDto = new ChannelDto(UUID.randomUUID(), ChannelType.PUBLIC, name,
            description, users, Instant.now());

        given(channelRepository.save(any(Channel.class))).willReturn(savedChannel);
        given(channelMapper.toDto(any(Channel.class))).willReturn(expectedDto);

        //When
        ChannelDto result = channelService.create(request);

        //Then
        assertThat(result).isEqualTo(expectedDto);
        then(channelRepository).should().save(any(Channel.class));
        then(channelMapper).should().toDto(any(Channel.class));

    }


    @Test
    @DisplayName("비공개 채널 생성 - 성공")
    void createPrivateChannel_Success() {
        //Given
        UUID user1Id = UUID.randomUUID();
        UUID user2Id = UUID.randomUUID();
        List<UUID> participantIds = Arrays.asList(user1Id, user2Id);

        PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(participantIds);
        UserDto userDto1 = new UserDto(user1Id, "user1", "user1@example.com", null, null);
        UserDto userDto2 = new UserDto(user2Id, "user2", "user2@example.com", null, null);
        List<UserDto> userDtoList = Arrays.asList(userDto1, userDto2);

        User user1 = new User("user1", "user1@example.com", "password", null);
        User user2 = new User("user2", "user2@example.com", "password", null);
        List<User> users = Arrays.asList(user1, user2);

        Channel savedChannel = new Channel(ChannelType.PRIVATE, null, null);
        ChannelDto expectedDto = new ChannelDto(UUID.randomUUID(), ChannelType.PUBLIC, null,
            null, userDtoList, Instant.now());

        List<ReadStatus> readStatusList = Arrays.asList(
            new ReadStatus(user1, savedChannel, Instant.now()),
            new ReadStatus(user2, savedChannel, Instant.now()));

        given(channelRepository.save(any(Channel.class))).willReturn(savedChannel);
        given(userRepository.findAllById(participantIds)).willReturn(users);
        given(channelMapper.toDto(any(Channel.class))).willReturn(expectedDto);

        //When
        ChannelDto result = channelService.create(request);

        //Then
        assertThat(result).isEqualTo(expectedDto);
        then(channelRepository).should().save(any(Channel.class));
        then(userRepository).should().findAllById(participantIds);
        then(channelMapper).should().toDto(any(Channel.class));
    }


    @Test
    @DisplayName("공개 채널 수정 - 성공")
    void update_Success() {
        //Given
        UUID channelId = UUID.randomUUID();
        String newName = "newTestChannel";
        String newDescription = "newTestDescription";
        PublicChannelUpdateRequest request = new PublicChannelUpdateRequest(newName,
            newDescription);
        List<UserDto> users = List.of();

        Channel existingChannel = new Channel(ChannelType.PUBLIC, "oldName", "oldDescription");
        ChannelDto expectedDto = new ChannelDto(channelId, ChannelType.PUBLIC, newName,
            newDescription, users, Instant.now());

        given(channelRepository.findById(channelId)).willReturn(Optional.of(existingChannel));
        given(channelMapper.toDto(existingChannel)).willReturn(expectedDto);

        //When
        ChannelDto result = channelService.update(channelId, request);

        //Then
        assertThat(result).isEqualTo(expectedDto);
        then(channelMapper).should().toDto(existingChannel);

    }

    @Test
    @DisplayName("채널 수정 - 비공개 채널 수정 시도로 실패")
    void update_FailWhenChannelIsNotPublic() {
        // Given
        UUID channelId = UUID.randomUUID();
        PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("New Name",
            "New description");

        Channel privateChannel = new Channel(ChannelType.PRIVATE, null, null);

        given(channelRepository.findById(channelId)).willReturn(Optional.of(privateChannel));

        // When & Then
        assertThatThrownBy(() -> channelService.update(channelId, request))
            .isInstanceOf(PrivateChannelUpdateException.class);

        then(channelRepository).should().findById(channelId);
        then(channelMapper).should(never()).toDto(any());

    }

    @Test
    @DisplayName("채널 삭제 - 성공")
    void delete_Success() {
        // Given
        UUID channelId = UUID.randomUUID();

        given(channelRepository.existsById(channelId)).willReturn(true);

        // When
        channelService.delete(channelId);

        // Then
        then(channelRepository).should().existsById(channelId);
        then(messageRepository).should().deleteAllByChannelId(channelId);
        then(readStatusRepository).should().deleteAllByChannelId(channelId);
        then(channelRepository).should().deleteById(channelId);
    }

    @Test
    @DisplayName("채널 삭제 - 채널을 찾을 수 없어 실패")
    void delete_FailWhenChannelNotFound() {
        // Given
        UUID channelId = UUID.randomUUID();

        given(channelRepository.existsById(channelId)).willReturn(false);

        // When & Then
        assertThatThrownBy(() -> channelService.delete(channelId))
            .isInstanceOf(ChannelNotFoundException.class);

        then(channelRepository).should().existsById(channelId);
        then(messageRepository).should(never()).deleteAllByChannelId(channelId);
        then(readStatusRepository).should(never()).deleteAllByChannelId(channelId);
        then(channelRepository).should(never()).deleteById(channelId);
    }

    @Test
    @DisplayName("사용자별 채널 조회 - 성공")
    void findAllByUserId_Success() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID channelId1 = UUID.randomUUID();
        UUID channelId2 = UUID.randomUUID();
        List<UserDto> users = List.of(new UserDto(userId, "name", "email", null, null));

        Channel channel1 = mock(Channel.class);
        Channel channel2 = mock(Channel.class);
        given(channel1.getId()).willReturn(channelId1);
        given(channel2.getId()).willReturn(channelId2);

        ReadStatus readStatus1 = new ReadStatus(null, channel1, Instant.now());
        ReadStatus readStatus2 = new ReadStatus(null, channel2, Instant.now());
        List<ReadStatus> readStatuses = Arrays.asList(readStatus1, readStatus2);

        List<UUID> subscribedChannelIds = Arrays.asList(channelId1, channelId2);
        List<Channel> channels = Arrays.asList(channel1, channel2);

        ChannelDto channelDto1 = new ChannelDto(channelId1, ChannelType.PRIVATE, null, null,
            users, Instant.now());
        ChannelDto channelDto2 = new ChannelDto(channelId2, ChannelType.PUBLIC, "Public Channel",
            "Description", users, Instant.now());
        List<ChannelDto> expectedDtos = Arrays.asList(channelDto1, channelDto2);

        given(readStatusRepository.findAllByUserId(userId)).willReturn(readStatuses);
        given(channelRepository.findAllByTypeOrIdIn(ChannelType.PUBLIC,
            subscribedChannelIds)).willReturn(channels);
        given(channelMapper.toDto(channel1)).willReturn(channelDto1);
        given(channelMapper.toDto(channel2)).willReturn(channelDto2);

        // When
        List<ChannelDto> result = channelService.findAllByUserId(userId);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyElementsOf(expectedDtos);
        then(readStatusRepository).should().findAllByUserId(userId);
        then(channelRepository).should()
            .findAllByTypeOrIdIn(ChannelType.PUBLIC, subscribedChannelIds);
    }

}
