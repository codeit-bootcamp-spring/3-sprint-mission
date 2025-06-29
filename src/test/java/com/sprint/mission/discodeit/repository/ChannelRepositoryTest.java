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
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class ChannelRepositoryTest {

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Channel createTestChannel(ChannelType type, String name) {
        Channel channel = new Channel(type, name, "설명: " + name);
        return channelRepository.save(channel);
    }

    @Test
    @DisplayName("PUBLIC 채널과 ID 목록에 포함된 PRIVATE 채널을 함께 조회")
    void shouldReturnPublicAndPrivateChannelsByTypeOrIdIn() {
        Channel publicChannel1 = createTestChannel(ChannelType.PUBLIC, "publicChannel1");
        Channel privateChannel1 = createTestChannel(ChannelType.PRIVATE, "privateChannel1");

        entityManager.flush();
        entityManager.clear();

        List<UUID> selectedPrivateIds = List.of(privateChannel1.getId());

        List<Channel> foundChannels = channelRepository.findAllByTypeOrIdIn(
            ChannelType.PUBLIC,
            selectedPrivateIds
        );

        assertThat(foundChannels).hasSize(2);
        assertThat(
            foundChannels.stream().anyMatch(c -> c.getType() == ChannelType.PUBLIC)).isTrue();
        assertThat(foundChannels.stream()
            .anyMatch(c -> c.getId().equals(privateChannel1.getId()))).isTrue();
    }

    @Test
    @DisplayName("PUBLIC 채널이 없고 ID 목록도 없으면 결과는 empty")
    void shouldReturnEmptyWhenNoPublicAndNoIds() {
        createTestChannel(ChannelType.PRIVATE, "privateChannel1");
        createTestChannel(ChannelType.PRIVATE, "privateChannel2");

        entityManager.flush();
        entityManager.clear();

        List<Channel> foundChannels = channelRepository.findAllByTypeOrIdIn(
            ChannelType.PUBLIC,
            List.of()
        );

        assertThat(foundChannels).isEmpty();
    }
}
