
package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.domain.Channel;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

class ChannelServiceTest {

    @Mock
    private ChannelRepository channelRepository;

    @InjectMocks
    private ChannelService channelService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("공개 채널 생성 성공")
    void createPublicChannel_success() {
        PublicChannelCreateRequest request = new PublicChannelCreateRequest("일반채널");
        Channel channel = new Channel(request.name(), true);

        given(channelRepository.save(channel)).willReturn(channel);

        Channel result = channelService.createPublicChannel(request);

        assertThat(result.getName()).isEqualTo("일반채널");
    }

    @Test
    @DisplayName("채널 조회 실패")
    void findChannel_notFound() {
        UUID id = UUID.randomUUID();
        given(channelRepository.findById(id)).willReturn(Optional.empty());

        assertThatThrownBy(() -> channelService.findById(id))
            .isInstanceOf(ChannelNotFoundException.class);
    }
}
