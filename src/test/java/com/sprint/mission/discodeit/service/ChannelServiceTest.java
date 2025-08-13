package com.sprint.mission.discodeit.service;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("채널 서비스 단위 테스트")
public class ChannelServiceTest {

    @Mock private ChannelRepository channelRepository;
    @Mock private ChannelMapper channelMapper;
    @Mock private UserRepository userRepository;

    @InjectMocks
    private BasicChannelService channelService;

    private Channel channel;
    private ChannelDto channelDto;

    @BeforeEach
    void setUp() {
        channel = new Channel(ChannelType.PUBLIC, "publicChannel", "This is public channel.");
        channelDto = new ChannelDto(
            UUID.randomUUID(),
            ChannelType.PUBLIC,
            "publicChannel",
            "This is public channel.",
            new ArrayList<UserDto>(),
            Instant.MIN
        );
    }

    @Test
    @DisplayName("공개 채널 생성 성공")
    void createPublicChannel() {
        // given
        PublicChannelCreateRequest channelCreateRequest = new PublicChannelCreateRequest("publicChannel", "This is public channel.");

        given(channelRepository.save(any(Channel.class))).willReturn(channel);
        given(channelMapper.toDto(any(Channel.class))).willReturn(channelDto);

        // when
        ChannelDto result = channelService.create(channelCreateRequest);

        // then
        then(channelRepository).should().save(any(Channel.class));
        then(channelMapper).should().toDto(any(Channel.class));

        assertThat(result).isNotNull();
        assertThat(result.type()).isEqualTo(ChannelType.PUBLIC);
        assertThat(result.name()).isEqualTo("publicChannel");
        assertThat(result.description()).isEqualTo("This is public channel.");
        assertThat(result).isSameAs(channelDto);
    }

    @Test
    @DisplayName("비공개 채널 생성 성공")
    void createPrivateChannel() {
        // given
        UUID userId = UUID.randomUUID();
        User user = new User("jane", "jane@test.com", "pw123456", null);
        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        Channel privateChannel = new Channel(ChannelType.PRIVATE, "privateChannel", "This is private channel.");
        ChannelDto privateChannelDto = new ChannelDto(
            UUID.randomUUID(),
            ChannelType.PRIVATE,
            "privateChannel",
            "This is private channel.",
            List.of(new UserDto(userId, "jane", "jane@test.com", null, false)),
            Instant.MIN
        );
        PrivateChannelCreateRequest privateChannelCreateRequest = new PrivateChannelCreateRequest(List.of(userId));

        given(channelRepository.save(any(Channel.class))).willReturn(privateChannel);
        given(channelMapper.toDto(any(Channel.class))).willReturn(privateChannelDto);

        // when
        ChannelDto result = channelService.create(privateChannelCreateRequest);

        // then
        then(userRepository).should().findById(userId);
        then(channelRepository).should().save(any(Channel.class));
        then(channelMapper).should().toDto(any(Channel.class));
        assertThat(result).isSameAs(privateChannelDto);
    }

    @Test
    @DisplayName("비공개 채널 생성 중 UserNotFoundException 예외 발생")
    void createPrivateChannelWithUserNotFound() {
        UUID userId = UUID.randomUUID();

        // given
        PrivateChannelCreateRequest privateChannelCreateRequest = new PrivateChannelCreateRequest(List.of(userId));
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> channelService.create(privateChannelCreateRequest))
            .isInstanceOf(UserNotFoundException.class);

        then(channelRepository).should(never()).save(any());
        then(channelMapper).should(never()).toDto(any());
    }

    @Test
    @DisplayName("공개 채널 수정 성공")
    void updatePublicChannel() {
        // given
        UUID channelId = UUID.randomUUID();
        PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("public", "update description");

        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));

        ChannelDto channelDto = new ChannelDto(
            channelId,
            ChannelType.PUBLIC,
            "public",
            "update description",
            List.of(),
            Instant.MIN
        );
        given(channelMapper.toDto(channel)).willReturn(channelDto);

        // when
        ChannelDto result = channelService.update(channelId, request);

        // then
        then(channelRepository).should().findById(channelId);
        assertThat(channel.getName()).isEqualTo("public");
        assertThat(channel.getDescription()).isEqualTo("update description");
        then(channelMapper).should().toDto(channel);
        assertThat(result).isSameAs(channelDto);
    }

    @Test
    @DisplayName("비공개 채널 수정 시 PrivateChannelUpdateException 예외 발생")
    void updatePrivateChannelWithPrivateChannelUpdateException() {
        // given
        UUID channelId = UUID.randomUUID();
        PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("private", "update description");

        Channel privateChannel = new Channel(ChannelType.PRIVATE, null, null);
        given(channelRepository.findById(channelId)).willReturn(Optional.of(privateChannel));

        // when & then
        assertThatThrownBy(() ->
            channelService.update(channelId, request)
        ).isInstanceOf(PrivateChannelUpdateException.class);

        then(channelRepository).should().findById(channelId);
        then(channelMapper).should(never()).toDto(any());
    }

    @Test
    @DisplayName("채널 삭제 성공")
    void deleteChannel() {
        // given
        UUID channelId = UUID.randomUUID();
        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
        willDoNothing().given(channelRepository).delete(channel);

        // when
        channelService.delete(channelId);

        // then
        then(channelRepository).should().findById(channelId);
        then(channelRepository).should().delete(channel);
    }

    @Test
    @DisplayName("채널 삭제 중 ChannelNotFoundException 발생")
    void deleteChannelWithChannelNotFoundException() {
        // given
        UUID channelId = UUID.randomUUID();
        given(channelRepository.findById(channelId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
            channelService.delete(channelId)
        ).isInstanceOf(ChannelNotFoundException.class);

        then(channelRepository).should().findById(channelId);
        then(channelRepository).should(never()).delete(any());
    }

    @Test
    @DisplayName("특정 사용자의 접속 채널 조회 성공")
    void findAllByUserId() {
        // given
        UUID userId = UUID.randomUUID();
        Channel ch1 = new Channel(ChannelType.PUBLIC, "ch1", "ch1");
        Channel ch2 = new Channel(ChannelType.PUBLIC, "ch2", "ch2");
        List<Channel> channels = List.of(ch1, ch2);
        given(channelRepository.findAllAccessible(ChannelType.PUBLIC, userId)).willReturn(channels);

        // when
        List<ChannelDto> result = channelService.findAllByUserId(userId);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).name()).isEqualTo("ch1");
        assertThat(result.get(0).description()).isEqualTo("ch1");
        assertThat(result.get(1).name()).isEqualTo("ch2");
        assertThat(result.get(1).description()).isEqualTo("ch2");
    }

}