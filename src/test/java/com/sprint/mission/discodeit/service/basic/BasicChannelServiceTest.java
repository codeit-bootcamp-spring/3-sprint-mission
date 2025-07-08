package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelDto;
import com.sprint.mission.discodeit.dto.channel.PublicChannelDto;
import com.sprint.mission.discodeit.dto.channel.PublicChannelUpdateDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.NotFoundChannelException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Channel 서비스 단위 테스트")
class BasicChannelServiceTest {

    @InjectMocks
    private BasicChannelService channelService;

    @Mock
    private ChannelRepository channelRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReadStatusRepository readStatusRepository;

    @Mock
    private ChannelMapper channelMapper;

    @Test
    @DisplayName("정상적인 Public 채널 생성 요청 시 올바른 비즈니스 로직이 수행되어야 한다.")
    void should_create_public_channel_when_valid_request() {

        // given
        UUID channelId = UUID.randomUUID();
        String name = "public1";
        String description = "It's public channel";

        PublicChannelDto request = new PublicChannelDto(name, description);

        Channel channel = Channel.builder()
                .name(name)
                .description(description)
                .type(ChannelType.PUBLIC)
                .build();

        ReflectionTestUtils.setField(channel, "id", channelId);

        ChannelResponseDto response = new ChannelResponseDto(channel.getId(), ChannelType.PUBLIC,
                name, description, null, null);

        given(channelRepository.save(any(Channel.class))).willReturn(channel);
        given(channelMapper.toDto(any(Channel.class))).willReturn(response);

        // when
        ChannelResponseDto result = channelService.createPublicChannel(request);

        // then
        assertNotNull(result);
        assertEquals(name, result.name());
        assertEquals(description, result.description());
        assertEquals(ChannelType.PUBLIC, result.type());
    }

    @Test
    @DisplayName("정상적인 Private 채널 생성 요청 시 올바른 비즈니스 로직이 수행되어야 한다.")
    void should_create_private_channel_when_valid_user_ids() {

        // given
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();

        PrivateChannelDto request = new PrivateChannelDto(List.of(userId1, userId2));

        Channel channel = new Channel();
        ReflectionTestUtils.setField(channel, "id", channelId);

        User user1 = User.builder()
                .username("user1")
                .email("user1@example.com")
                .password("pwd1")
                .build();

        User user2 = User.builder()
                .username("user2")
                .email("user2@example.com")
                .password("pwd2")
                .build();

        ChannelResponseDto response = new ChannelResponseDto(channelId, ChannelType.PRIVATE, null,
                null, List.of(), null);

        ReflectionTestUtils.setField(user1, "id", userId1);
        ReflectionTestUtils.setField(user2, "id", userId2);

        given(channelRepository.save(any(Channel.class))).willReturn(channel);
        given(userRepository.findById(userId1)).willReturn(Optional.of(user1));
        given(userRepository.findById(userId2)).willReturn(Optional.of(user2));
        given(channelMapper.toDto(channel)).willReturn(response);

        // when
        ChannelResponseDto result = channelService.createPrivateChannel(request);

        // then
        assertNotNull(result);
        assertEquals(channelId, result.id());
        assertEquals(ChannelType.PRIVATE, result.type());
        verify(channelRepository).save(any(Channel.class));
        verify(readStatusRepository).saveAll(anyList());
        verify(channelMapper).toDto(channel);
    }

    @Test
    @DisplayName("등록된 채널 ID로 조회하면 올바르게 조회되어야 한다.")
    void should_return_channel_when_channel_exists_by_id() {

        // given
        String channelName = "channel1";
        String channelDescription = "test channel";
        UUID channelId = UUID.randomUUID();

        Channel channel = Channel.builder()
                .name(channelName)
                .description(channelDescription)
                .type(ChannelType.PUBLIC)
                .build();

        ReflectionTestUtils.setField(channel, "id", channelId);

        ChannelResponseDto expectedChannel = new ChannelResponseDto(channelId, ChannelType.PUBLIC, channelName,
                channelDescription, List.of(), null);

        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
        given(channelMapper.toDto(channel)).willReturn(expectedChannel);

        // when
        ChannelResponseDto result = channelService.findById(channelId);

        // then
        assertEquals(expectedChannel, result);
        verify(channelRepository).findById(channelId);
        verify(channelMapper).toDto(channel);
    }

    @Test
    @DisplayName("등록되지 않은 채널 ID로 조회하면 NotFoundChannelException이 발생해야 한다.")
    void should_throw_not_found_exception_when_channel_does_not_exist_by_id() {

        // given
        UUID notExistId = UUID.randomUUID();
        given(channelRepository.findById(notExistId)).willReturn(Optional.empty());

        // when
        Throwable thrown = catchThrowable(() -> channelService.findById(notExistId));

        // then
        assertThat(thrown)
                .isInstanceOf(NotFoundChannelException.class)
                .hasMessageContaining("채널");
        verify(channelRepository).findById(notExistId);
    }

    @Test
    @DisplayName("등록된 사용자 ID로 조회하면 사용자의 참여 채널과 공개 채널이 모두 반환되어야 한다.")
    void should_return_user_channels_and_public_channels_when_user_exists() {

        // given
        UUID userId = UUID.randomUUID();
        UUID publicChannelId = UUID.randomUUID();
        UUID privateChannelId = UUID.randomUUID();
        UUID notRelatedChannelId = UUID.randomUUID();

        User user = User.builder()
                .username("test")
                .email("test@test.com")
                .password("test123")
                .build();

        Channel publicChannel = Channel.builder()
                .name("public")
                .description("test channel")
                .type(ChannelType.PUBLIC)
                .build();

        Channel privateChannel = Channel.builder()
                .type(ChannelType.PRIVATE)
                .build();

        Channel notRelatedChannel = Channel.builder()
                .type(ChannelType.PRIVATE)
                .build();

        ReflectionTestUtils.setField(user, "id", userId);
        ReflectionTestUtils.setField(publicChannel, "id", publicChannelId);
        ReflectionTestUtils.setField(privateChannel, "id", privateChannelId);
        ReflectionTestUtils.setField(notRelatedChannel, "id", notRelatedChannelId);

        ReadStatus readStatus = ReadStatus.builder()
                .channel(privateChannel)
                .user(user)
                .build();
        ReflectionTestUtils.setField(readStatus, "id", UUID.randomUUID());

        List<Channel> allChannels = List.of(publicChannel, privateChannel, notRelatedChannel);
        List<ReadStatus> readStatuses = List.of(readStatus);

        ChannelResponseDto publicChannelDto = new ChannelResponseDto(publicChannelId, ChannelType.PUBLIC, "public",
                "test channel", null, null);

        ChannelResponseDto privateChannelDto = new ChannelResponseDto(privateChannelId, ChannelType.PRIVATE, null,
                null, List.of(), null);

        given(readStatusRepository.findAllByUserId(userId)).willReturn(readStatuses);
        given(channelRepository.findAll()).willReturn(allChannels);
        given(channelMapper.toDto(publicChannel)).willReturn(publicChannelDto);
        given(channelMapper.toDto(privateChannel)).willReturn(privateChannelDto);

        // when
        List<ChannelResponseDto> result = channelService.findAllByUserId(userId);

        // then
        assertEquals(2, result.size());
        assertTrue(result.contains(publicChannelDto));
        assertTrue(result.contains(privateChannelDto));
        assertFalse(result.stream().anyMatch(dto -> dto.id().equals(notRelatedChannelId)));
        verify(readStatusRepository).findAllByUserId(userId);
        verify(channelRepository).findAll();
        verify(channelMapper).toDto(publicChannel);
        verify(channelMapper).toDto(privateChannel);
        verifyNoMoreInteractions(channelMapper); // 참여하지 않은 비공개 채널은 변환 X
    }

    @Test
    @DisplayName("등록되지 않은 사용자 ID로 조회하면 Public 채널만 조회되어야한다.")
    void should_return_only_public_channels_when_user_does_not_exist() {

        // given
        UUID notExistId = UUID.randomUUID();
        UUID publicChannelId = UUID.randomUUID();
        UUID privateChannelId = UUID.randomUUID();

        Channel publicChannel = Channel.builder()
                .name("public")
                .description("test channel")
                .type(ChannelType.PUBLIC)
                .build();

        Channel privateChannel = Channel.builder()
                .type(ChannelType.PRIVATE)
                .build();

        ReflectionTestUtils.setField(publicChannel, "id", publicChannelId);
        ReflectionTestUtils.setField(privateChannel, "id", privateChannelId);

        List<Channel> allChannels = List.of(publicChannel, privateChannel);

        ChannelResponseDto publicChannelDto = new ChannelResponseDto(publicChannelId, ChannelType.PUBLIC, "public",
                "test channel", null, null);

        given(readStatusRepository.findAllByUserId(notExistId)).willReturn(List.of());
        given(channelRepository.findAll()).willReturn(allChannels);
        given(channelMapper.toDto(publicChannel)).willReturn(publicChannelDto);

        // when
        List<ChannelResponseDto> result = channelService.findAllByUserId(notExistId);

        // then
        assertEquals(1, result.size());
        assertTrue(result.contains(publicChannelDto));
        assertFalse(result.stream().anyMatch(dto -> dto.id().equals(privateChannelId)));
        verify(readStatusRepository).findAllByUserId(notExistId);
        verify(channelRepository).findAll();
        verify(channelMapper).toDto(publicChannel);
        verifyNoMoreInteractions(channelMapper);
    }

    @Test
    @DisplayName("정상적인 요청으로 Public 채널을 업데이트하면 올바른 비즈니스 로직이 수행되어야한다.")
    void should_update_public_channel_when_valid_update_request() {

        // given
        UUID channelId = UUID.randomUUID();
        String newName = "public";
        String newDescription = "It's public channel";

        Channel existingChannel = Channel.builder()
                .name("public channel")
                .description("it's public channel")
                .type(ChannelType.PUBLIC)
                .build();

        ReflectionTestUtils.setField(existingChannel, "id", channelId);

        PublicChannelUpdateDto updateRequest = new PublicChannelUpdateDto(newName, newDescription);

        ChannelResponseDto expectedResponse = new ChannelResponseDto(channelId, ChannelType.PUBLIC,
                newName, newDescription, List.of(), null);

        given(channelRepository.findById(channelId)).willReturn(Optional.of(existingChannel));
        given(channelRepository.save(any(Channel.class))).willReturn(existingChannel);
        given(channelMapper.toDto(any(Channel.class))).willReturn(expectedResponse);

        // when
        ChannelResponseDto result = channelService.update(channelId, updateRequest);

        // then
        assertNotNull(result);
        assertEquals(newName, result.name());
        assertEquals(newDescription, result.description());
        verify(channelRepository).save(any(Channel.class));
    }

    @Test
    @DisplayName("Private 채널을 수정하려하면 PrivateChannelUpdateException이 발생해야한다.")
    void should_throw_exception_when_try_to_update_private_channel() {

        // given
        UUID channelId = UUID.randomUUID();
        Channel channel = Channel.builder()
                .type(ChannelType.PRIVATE)
                .build();

        ReflectionTestUtils.setField(channel, "id", channelId);

        PublicChannelUpdateDto updateRequest = new PublicChannelUpdateDto("new Channel", "It's new Channel");

        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));

        // when
        Throwable thrown = catchThrowable(() -> channelService.update(channelId, updateRequest));

        // then
        assertThat(thrown)
                .isInstanceOf(PrivateChannelUpdateException.class)
                .hasMessageContaining("Private");
        verify(channelRepository).findById(channelId);
        verify(userRepository, never()).save(any());
    }


    @Test
    @DisplayName("등록된 채널 ID로 삭제를 시도하면 정상적으로 삭제되어야한다.")
    void should_delete_channel_when_channel_exists_by_id() {

        // given
        UUID channelId = UUID.randomUUID();

        Channel channel = Channel.builder()
                .name("public channel")
                .description("It's public channel")
                .type(ChannelType.PUBLIC)
                .build();

        ReflectionTestUtils.setField(channel, "id", channelId);

        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));

        // when
        channelService.deleteById(channelId);

        // then
        verify(channelRepository).deleteById(channelId);
    }

    @Test
    @DisplayName("등록되지 않은 채널 ID로 삭제를 시도하면 NotFoundChannelException이 발생해야 한다.")
    void should_throw_not_found_exception_when_try_to_delete_nonexistent_channel() {

        // given
        UUID notExistId = UUID.randomUUID();
        given(channelRepository.findById(notExistId)).willReturn(Optional.empty());

        // when
        Throwable thrown = catchThrowable(() -> channelService.deleteById(notExistId));

        // then
        assertThat(thrown)
                .isInstanceOf(NotFoundChannelException.class)
                .hasMessageContaining("채널");
        verify(channelRepository).findById(notExistId);
    }
}