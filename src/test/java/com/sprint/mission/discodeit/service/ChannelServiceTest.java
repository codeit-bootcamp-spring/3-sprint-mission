package com.sprint.mission.discodeit.service;

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
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateDeniedException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("채널 서비스 단위테스트")
public class ChannelServiceTest {

    @Mock private ChannelMapper channelMapper;
    @Mock private ChannelRepository channelRepository;
    @Mock private ReadStatusRepository readStatusRepository;
    @Mock private MessageRepository messageRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks private BasicChannelService channelService;

    private Channel publicChannel;
    private ChannelDto puclicChannelDto;

    @BeforeEach
    public void setUp() {
        publicChannel = new Channel(ChannelType.PUBLIC, "test Channel", "test Description");
        puclicChannelDto = new ChannelDto(
                UUID.randomUUID(), ChannelType.PUBLIC, "test Channel", "test Description", new ArrayList<UserDto>(), null
        );
    }

    @Test
    @DisplayName("정상적인 요청을 통해 PUBLIC 채널을 생성할 수 있다")
    void shouldCreatePublicChannelWhenRequestValid() {
        // given
        PublicChannelCreateRequest publicChannelCreateRequest = new PublicChannelCreateRequest("new Channel", "new Description");

        Channel channel = new Channel(ChannelType.PUBLIC, "new Channel", "new Description");
        ChannelDto channelDto = new ChannelDto(
                UUID.randomUUID(), ChannelType.PUBLIC, "new Channel", "new Description", new ArrayList<UserDto>(), null
        );

        given(channelRepository.save(any(Channel.class))).willReturn(channel);
        given(channelMapper.toDto(any(Channel.class))).willReturn(channelDto);

        // when
        ChannelDto result = channelService.create(publicChannelCreateRequest);

        // then
        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("new Channel");
        assertThat(result.description()).isEqualTo("new Description");
        assertThat(result.type()).isEqualTo(ChannelType.PUBLIC);
    }

    @Test
    @DisplayName("정상적인 요청을 통해 PRIVATE 채널을 생성할 수 있다")
    void shouldCreatePrivateChannelWhenRequestValid() {
        // given
        PrivateChannelCreateRequest privateChannelCreateRequest = new PrivateChannelCreateRequest(new ArrayList<>());

        Channel channel = new Channel(ChannelType.PRIVATE, "private Channel", "private Description");
        ChannelDto channelDto = new ChannelDto(
                UUID.randomUUID(), ChannelType.PRIVATE, "private Channel", "private Description", new ArrayList<UserDto>(), null
        );

        given(channelRepository.save(any(Channel.class))).willReturn(channel);
        given(channelMapper.toDto(any(Channel.class))).willReturn(channelDto);

        // when
        ChannelDto result = channelService.create(privateChannelCreateRequest);

        // then
        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("private Channel");
        assertThat(result.description()).isEqualTo("private Description");
        assertThat(result.type()).isEqualTo(ChannelType.PRIVATE);
    }

    @Test
    @DisplayName("정상적인 요청을 통해 채널을 수정할 수 있다")
    void shouldUpdatePublicChannelWhenRequestValid() {
        // given
        UUID channelId = publicChannel.getId();
        PublicChannelUpdateRequest publicChannelUpdateRequest = new PublicChannelUpdateRequest("new Channel", "new Description");

        given(channelRepository.findById(channelId)).willReturn(Optional.of(publicChannel));
        given(channelRepository.save(any(Channel.class))).willReturn(publicChannel);
        given(channelMapper.toDto(any(Channel.class))).willReturn(puclicChannelDto);

        // when
        ChannelDto result = channelService.update(channelId, publicChannelUpdateRequest);

        // then
        assertThat(result).isNotNull();

        ArgumentCaptor<Channel> captor = ArgumentCaptor.forClass(Channel.class);
        verify(channelRepository).save(captor.capture());
        Channel updated = captor.getValue();

        assertThat(updated.getName()).isEqualTo("new Channel");
        assertThat(updated.getDescription()).isEqualTo("new Description");
    }

    @Test
    @DisplayName("존재하지 않는 채널 ID로 수정 요청 시 예외 발생")
    void shouldNotUpdateWhenRequestInvalid() {
        // given
        UUID channelId = UUID.randomUUID();
        PublicChannelUpdateRequest publicChannelUpdateRequest = new PublicChannelUpdateRequest("new Channel", "new Description");

        given(channelRepository.findById(channelId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> channelService.update(channelId, publicChannelUpdateRequest))
                .isInstanceOf(ChannelNotFoundException.class);
    }

    @Test
    @DisplayName("PRIVATE 채널 수정 요청 시 예외 발생")
    void shouldNotUpdateWhenPrivateChannel() {
        // given
        UUID channelId = UUID.randomUUID();
        Channel privateChannel = new Channel(ChannelType.PRIVATE, "test Channel", "test Description");
        PublicChannelUpdateRequest publicChannelUpdateRequest = new PublicChannelUpdateRequest("new Channel", "new Description");

        given(channelRepository.findById(channelId)).willReturn(Optional.of(privateChannel));

        // when & then
        assertThatThrownBy(() -> channelService.update(channelId, publicChannelUpdateRequest))
                .isInstanceOf(PrivateChannelUpdateDeniedException.class);
    }

    @Test
    @DisplayName("정상적인 요청을 통해 채널을 삭제할 수 있다")
    void shouldDeleteChannelWhenRequestValid() {
        // given
        UUID channelId = publicChannel.getId();

        given(channelRepository.findById(channelId)).willReturn(Optional.of(publicChannel));

        // when

        channelService.delete(channelId);

        // then
        verify(channelRepository).deleteById(channelId);
        verify(readStatusRepository).deleteAllByChannelId(channelId);
        verify(messageRepository).deleteAllByChannelId(channelId);
    }

    @Test
    @DisplayName("존재하지 않는 채널 ID로 삭제 요청 시 예외 발생")
    void shouldNotDeleteWhenNotFoundChannelId() {
        // given
        UUID channelId = UUID.randomUUID();

        given(channelRepository.findById(channelId)).willReturn(Optional.empty());
        // when & then
        assertThatThrownBy(() -> channelService.delete(channelId))
                .isInstanceOf(ChannelNotFoundException.class);
    }

    @Test
    @DisplayName("정상적인 요청을 통해 해당 사용자의 채널 목록을 조회할 수 있다")
    void shouldFindAllChannelsByUserId() {
        // given
        User user = new User("test", "test@gmail.com", "000000001", null);
        UUID userId = user.getId();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        Channel publicChannel = new Channel(ChannelType.PUBLIC, "public", "공개 채널");
        UUID publicChannelId = UUID.randomUUID();
        ReflectionTestUtils.setField(publicChannel, "id", publicChannelId);

        Channel privateChannel = new Channel(ChannelType.PRIVATE, "private", "비공개 채널");
        UUID privateChannelId = UUID.randomUUID();
        ReflectionTestUtils.setField(privateChannel, "id", privateChannelId);

        ReadStatus readStatus = mock(ReadStatus.class);
        given(readStatus.getChannel()).willReturn(privateChannel);

        given(channelRepository.findAll()).willReturn(List.of(publicChannel, privateChannel));
        given(readStatusRepository.findAllByUserId(userId)).willReturn(List.of(readStatus));

        ChannelDto publicDto = new ChannelDto(publicChannelId, ChannelType.PUBLIC, "public", "공개 채널", null, null);
        ChannelDto privateDto = new ChannelDto(privateChannelId, ChannelType.PRIVATE, "private", "비공개 채널", null, null);

        given(channelMapper.toDto(publicChannel)).willReturn(publicDto);
        given(channelMapper.toDto(privateChannel)).willReturn(privateDto);

        // when
        List<ChannelDto> result = channelService.findAllByUserId(userId);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(ChannelDto::type)
                .containsExactlyInAnyOrder(ChannelType.PUBLIC, ChannelType.PRIVATE);

        verify(channelRepository).findAll();
        verify(readStatusRepository).findAllByUserId(userId);
    }

    @Test
    @DisplayName("존재하지 않는 유저 ID로 채널 목록 조회 시 예외 발생")
    void shouldNotFindAllChannelsByUserId() {
        // given
        UUID userId = UUID.randomUUID();

        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> channelService.findAllByUserId(userId))
                .isInstanceOf(UserNotFoundException.class);
    }
}
