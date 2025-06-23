package com.sprint.mission.discodeit.repository;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@EnableJpaAuditing
@ActiveProfiles("test")
public class ChannelRepositoryTest {

    @Autowired
    private ChannelRepository channelRepository;

    @Test
    @DisplayName("유저가 속한 모든 채널 조회 - case : success")
    void findAllByUserIdSuccess() {
        Channel channel1 = new Channel(ChannelType.PUBLIC, "testChannel1", "testChannel1 description");
        Channel channel2 = new Channel(ChannelType.PUBLIC, "testChannel2", "testChannel2 description");
        Channel channel3 = new Channel(ChannelType.PRIVATE, null, null);
        channelRepository.saveAll(List.of(channel1,channel2,channel3));

        List<Channel> result = channelRepository.findAllByTypeOrIdIn(ChannelType.PUBLIC, List.of(channel3.getId()));

        assertEquals(3, result.size());
        List<String> names = result.stream().map(Channel::getName).toList();
        assertTrue(names.contains("testChannel1"));
    }

    @Test
    @DisplayName("유저가 속한 모든 채널 조회 - case : 일치하는 항목이 없음으로 인한 failed")
    void findAllByUserIdFail() {
        Channel channel1 = new Channel(ChannelType.PUBLIC, "testChannel1", "testChannel1 description");
        Channel channel2 = new Channel(ChannelType.PRIVATE, null, null);
        channelRepository.saveAll(List.of(channel1,channel2));

        UUID nonExistId = UUID.randomUUID();
        List<Channel> result = channelRepository.findAllByTypeOrIdIn(ChannelType.PUBLIC, List.of(nonExistId));

        assertEquals(1, result.size());
    }

}
