package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class ChannelRepositoryTest {

    @Autowired
    ChannelRepository channelRepository;

    @Test
    @DisplayName("채널 조회 성공 - PUBLIC 타입 또는 ID 목록에 포함된 채널")
    void shouldFindChannelsByTypeOrIdInList() {
        Channel publicChannel = channelRepository.save(
            new Channel(ChannelType.PUBLIC, "public1", null));
        Channel privateChannel = channelRepository.save(
            new Channel(ChannelType.PRIVATE, "private1", null));

        List<Channel> result = channelRepository.findAllByTypeOrIdIn(
            ChannelType.PUBLIC,
            List.of(privateChannel.getId())
        );

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Channel::getName)
            .containsExactlyInAnyOrder("public1", "private1");
    }

    @Test
    @DisplayName("채널 조회 실패 - PUBLIC 타입과 ID 목록이 모두 없는 경우")
    void shouldReturnEmpty_whenNoMatchingChannelsFound() {
        channelRepository.save(new Channel(ChannelType.PRIVATE, "private1", null));

        List<Channel> result = channelRepository.findAllByTypeOrIdIn(
            ChannelType.PUBLIC,
            List.of(UUID.randomUUID())
        );

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("채널 페이징 조회 성공 - 페이지 요청에 따른 채널 조회")
    void shouldReturnPagedChannels_whenPagingIsRequested() {
        channelRepository.save(new Channel(ChannelType.PUBLIC, "channel1", null));
        channelRepository.save(new Channel(ChannelType.PUBLIC, "channel2", null));

        Page<Channel> result = channelRepository.findAll(PageRequest.of(0, 1));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(2);
    }

    @Test
    @DisplayName("채널 페이징 조회 실패 - 데이터가 없을 경우")
    void shouldReturnEmptyPage_whenNoChannelsExist() {
        Page<Channel> result = channelRepository.findAll(PageRequest.of(0, 1));

        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
    }
}
