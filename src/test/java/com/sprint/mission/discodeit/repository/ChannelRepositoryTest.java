package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.config.JpaAuditingConfig;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;


@DataJpaTest
@Import(JpaAuditingConfig.class)
@ActiveProfiles("test") // application-test.yaml 사용

class ChannelRepositoryTest {

    @Autowired
    private ChannelRepository channelRepository;

    @Test
    @DisplayName("채널 저장 및 조회")
    void test() {
        // given
        Channel channel = new Channel(ChannelType.PUBLIC, "testChannel", "test");

        // when
        Channel result = channelRepository.saveAndFlush(channel);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(channel.getName());
        assertThat(result.getDescription()).isEqualTo(channel.getDescription());
    }


    @Test
    @DisplayName("Public 채널만 조회")
    void findAllByPublicType_success() {
        // given
        Channel channel1 = new Channel(ChannelType.PUBLIC, "publicChannel1", "test1");
        Channel channel2 = new Channel(ChannelType.PUBLIC, "publicChannel2", "test2");
        Channel channel3 = new Channel(ChannelType.PRIVATE, null, null);

        channelRepository.saveAllAndFlush(List.of(channel1, channel2, channel3));

        // when
        List<Channel> result = channelRepository.findAllByTypeOrIdIn(ChannelType.PUBLIC, List.of());

        // then
        assertThat(result).hasSize(2);
        assertThat(result).allMatch(channel -> channel.getType().equals(ChannelType.PUBLIC));
    }

    @Test
    @DisplayName("Private 채널만 조회")
    void findAllByPrivateType_success() {
        // given
        Channel channel1 = new Channel(ChannelType.PUBLIC, "publicChannel1", "test1");
        Channel channel2 = new Channel(ChannelType.PUBLIC, "publicChannel2", "test2");
        Channel channel3 = new Channel(ChannelType.PRIVATE, null, null);

        channelRepository.saveAllAndFlush(List.of(channel1, channel2, channel3));

        // when
        List<Channel> result = channelRepository.findAllByTypeOrIdIn(ChannelType.PRIVATE,
            List.of());

        // then
        assertThat(result).hasSize(1);
        assertThat(result).allMatch(channel -> channel.getType().equals(ChannelType.PRIVATE));
    }

    @Test
    @DisplayName("모든 채널 조회")
    void findAllByIdIn_success() {
        // given
        Channel channel1 = new Channel(ChannelType.PUBLIC, "publicChannel1", "test1");
        Channel channel2 = new Channel(ChannelType.PUBLIC, "publicChannel2", "test2");
        Channel channel3 = new Channel(ChannelType.PRIVATE, null, null);

        channelRepository.saveAllAndFlush(List.of(channel1, channel2, channel3));

        List<UUID> ids = List.of(channel1.getId(), channel2.getId(), channel3.getId());

        // when
        List<Channel> result = channelRepository.findAllByTypeOrIdIn(ChannelType.PRIVATE,
            ids);

        // then
        assertThat(result).hasSize(3);
    }

    @Test
    @DisplayName("일치하는 채널 없을 경우 빈 리스트 반환")
    void findAllByTypeOrIdIn_emptyResult() {
        // when
        List<Channel> result = channelRepository.findAllByTypeOrIdIn(ChannelType.PUBLIC, List.of());

        // then
        assertThat(result).isEmpty();
    }
}