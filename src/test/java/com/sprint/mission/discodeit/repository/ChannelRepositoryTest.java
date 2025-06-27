package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("ChannelRepository 기능 테스트")
class ChannelRepositoryTest {

    @Autowired
    private ChannelRepository channelRepository;

    @Test
    @DisplayName("채널을 등록하고 조회할 수 있어야 한다.")
    void itShouldSaveAndFindChannel() {

        // given
        Channel channel = Channel.builder()
                .name("public")
                .description("test channel")
                .type(ChannelType.PUBLIC)
                .build();

        // when
        Channel savedChannel = channelRepository.save(channel);
        Optional<Channel> foundChannel = channelRepository.findById(savedChannel.getId());

        // then
        assertTrue(foundChannel.isPresent());
        assertEquals("public", foundChannel.get().getName());
        assertEquals("test channel", foundChannel.get().getDescription());
    }
}