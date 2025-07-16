package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.config.AppConfig;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("ChannelRepository 슬라이스 테스트")
@Import(AppConfig.class)
class ChannelRepositoryTest {

    @Autowired
    private ChannelRepository channelRepository;

    @Test
    @DisplayName("공개 채널 저장하고 조회")
    void save() {
        // Given
        Channel channel = new Channel(ChannelType.PUBLIC, "채널1", "테스트 채널입니다.");
        channelRepository.save(channel);

        // When
        Channel result = channelRepository.findById(channel.getId())
            .orElseThrow(() -> new ChannelNotFoundException(channel.getId()));

        // Then
        assertThat(result).isEqualTo(channel);
        assertThat(result.getType()).isEqualTo(channel.getType());
        assertThat(result.getName()).isEqualTo("채널1");
        assertThat(result.getDescription()).isEqualTo("테스트 채널입니다.");
    }
}