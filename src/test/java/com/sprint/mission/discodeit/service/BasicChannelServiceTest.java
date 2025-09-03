package com.sprint.mission.discodeit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;

import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
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
    @DisplayName("공개 채널 생성 성공")
    void shouldCreatePublicChannelSuccessfully() {
        PublicChannelCreateRequest request = new PublicChannelCreateRequest("test", "desc");
        Channel channel = new Channel(ChannelType.PUBLIC, "test", "desc");

        given(channelRepository.save(any())).willReturn(channel);
        given(channelMapper.toResponse(any())).willReturn(
            new ChannelResponse(UUID.randomUUID(), ChannelType.PUBLIC, "test", "desc", List.of(),
                null)
        );

        ChannelResponse response = channelService.createPublicChannel(request);

        assertThat(response.name()).isEqualTo("test");
    }

    @Test
    @DisplayName("비공개 채널 생성 성공")
    void shouldCreatePrivateChannelSuccessfully() {
        UUID userId = UUID.randomUUID();
        PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(List.of(userId));

        given(userRepository.findAllById(request.participantIds()))
            .willReturn(List.of(
                new com.sprint.mission.discodeit.entity.User("username", "email", "password",
                    null, Role.USER)));

        given(channelMapper.toResponse(any())).willReturn(
            new ChannelResponse(UUID.randomUUID(), ChannelType.PRIVATE, null, null, List.of(), null)
        );

        ChannelResponse response = channelService.createPrivateChannel(request);

        assertThat(response.type()).isEqualTo(ChannelType.PRIVATE);
    }

    @Test
    @DisplayName("존재하지 않는 채널 조회 시 예외 발생")
    void shouldThrowException_whenChannelNotFound() {
        UUID id = UUID.randomUUID();
        given(channelRepository.findById(id)).willReturn(Optional.empty());

        assertThatThrownBy(() -> channelService.find(id))
            .isInstanceOf(ChannelNotFoundException.class)
            .hasMessageContaining("Channel not found");
    }

    @Test
    @DisplayName("비공개 채널 수정 시 예외 발생")
    void shouldThrowException_whenUpdatingPrivateChannel() {
        UUID id = UUID.randomUUID();
        Channel privateChannel = new Channel(ChannelType.PRIVATE, null, null);

        given(channelRepository.findById(id)).willReturn(Optional.of(privateChannel));
        PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("new", "desc");

        assertThatThrownBy(() -> channelService.update(id, request))
            .isInstanceOf(PrivateChannelUpdateException.class)
            .hasMessage("Private channels cannot be updated.");
    }

    @Test
    @DisplayName("존재하지 않는 채널 삭제 시 예외 발생")
    void shouldThrowException_whenDeletingNonExistentChannel() {
        UUID id = UUID.randomUUID();
        given(channelRepository.existsById(id)).willReturn(false);

        assertThatThrownBy(() -> channelService.delete(id))
            .isInstanceOf(ChannelNotFoundException.class)
            .hasMessageContaining("Channel not found");
    }
}
