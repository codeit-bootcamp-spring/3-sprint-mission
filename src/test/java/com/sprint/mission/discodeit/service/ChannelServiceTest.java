package com.sprint.mission.discodeit.service;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.never;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
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
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@DisplayName("채널 서비스 단위 테스트")
public class ChannelServiceTest {

    @Mock private ChannelRepository channelRepository;
    @Mock private ChannelMapper channelMapper;
    @Mock private UserRepository userRepository;
    @Mock private ReadStatusRepository readStatusRepository;

    @InjectMocks
    private BasicChannelService channelService;

    private UUID userId;
    private UUID channelId;
    private User user;
    private UserDto userDto;
    private Channel channel;
    private ChannelDto channelDto;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        channelId = UUID.randomUUID();

        user = new User("jane", "jane@test.com", "pw123456", null);
        userDto = new UserDto(userId, "jane", "jane@test.com", null, false, Role.USER);
        channel = new Channel(ChannelType.PUBLIC, "publicChannel", "This is public channel.");
        ReflectionTestUtils.setField(channel, "id", channelId);
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
        given(userRepository.findAllById(List.of(userId))).willReturn(List.of(user));

        Channel privateChannel = new Channel(ChannelType.PRIVATE, "privateChannel", "This is private channel.");
        ChannelDto privateChannelDto = new ChannelDto(
            UUID.randomUUID(),
            ChannelType.PRIVATE,
            "privateChannel",
            "This is private channel.",
            List.of(userDto),
            Instant.MIN
        );
        PrivateChannelCreateRequest privateChannelCreateRequest = new PrivateChannelCreateRequest(List.of(userId));

        given(channelRepository.save(any(Channel.class))).willReturn(privateChannel);
        given(channelMapper.toDto(any(Channel.class))).willReturn(privateChannelDto);

        // when
        ChannelDto result = channelService.create(privateChannelCreateRequest);

        // then
        then(userRepository).should().findAllById(List.of(userId));
        then(channelRepository).should().save(any(Channel.class));
        then(channelMapper).should().toDto(any(Channel.class));
        assertThat(result).isSameAs(privateChannelDto);
    }

    @Test
    @DisplayName("공개 채널 수정 성공")
    void updatePublicChannel() {
        // given
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
        List<ReadStatus> readStatuses = List.of(new ReadStatus(user, channel, Instant.now()));
        given(readStatusRepository.findAllByUserId(eq(userId))).willReturn(readStatuses);
        given(channelRepository.findAllByTypeOrIdIn(ChannelType.PUBLIC, List.of(channel.getId()))).willReturn(List.of(channel));
        given(channelMapper.toDto(any(Channel.class))).willReturn(channelDto);

        // when
        List<ChannelDto> result = channelService.findAllByUserId(userId);

        // then
        assertThat(result).containsExactly(channelDto);
    }

}