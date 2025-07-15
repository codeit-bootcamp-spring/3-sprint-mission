package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@EnableJpaAuditing
@ActiveProfiles("test")
class ChannelRepositoryTest {

    @Autowired
    private ChannelRepository channelRepository;

    private Channel publicChannel;
    private Channel privateChannel;

    @BeforeEach
    void setUp() {
        publicChannel = new Channel(ChannelType.PUBLIC, "공개채널", "공개채널 입니다.");
        privateChannel = new Channel(ChannelType.PRIVATE, "비공개채널", "비공개 채널 입니다.");

        channelRepository.saveAll(List.of(publicChannel, privateChannel));
    }

    @Test
    @DisplayName("공개채널 또는 Ids에 포함된 채널 전체 조회 성공")
    void findAllByTypeOrIdIn_success() {
        //given
        List<UUID> mySubscribedChannelIds = List.of(privateChannel.getId());

        //when
        List<Channel> result = channelRepository.findAllByTypeOrIdIn(ChannelType.PUBLIC,
            mySubscribedChannelIds);

        //then
        assertThat(result)
            .hasSize(2)
            .extracting(Channel::getId)
            .containsExactlyInAnyOrder(publicChannel.getId(), privateChannel.getId());
    }

    @Test
    @DisplayName("조건에 맞는 채널이 없을 경우 빈 리스트 반환")
    void findAllByTypeOrIdIn_notFound() {
        //given
        channelRepository.delete(publicChannel);
        List<UUID> unrelatedIds = List.of(UUID.randomUUID());

        //when
        List<Channel> result = channelRepository.findAllByTypeOrIdIn(ChannelType.PUBLIC,
            unrelatedIds);

        //then
        assertThat(result).isEmpty();
    }
}
