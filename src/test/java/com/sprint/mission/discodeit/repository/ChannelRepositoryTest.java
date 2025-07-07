package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
@DisplayName("ChannelRepository 슬라이스 테스트")
public class ChannelRepositoryTest {

    @Autowired
    private ChannelRepository channelRepository;

    @Test
    @DisplayName("채널을 저장하고 조회할 수 있다")
    void saveAndFindChannel() {
        // given
        Channel channel = new Channel(ChannelType.PRIVATE, "test", "test");

        // when
        Channel result = channelRepository.save(channel);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("test");
        assertThat(result.getDescription()).isEqualTo("test");
    }
}
