package com.sprint.mission.discodeit.slice.repository;

import com.sprint.mission.discodeit.config.QuerydslConfig;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.repository.jpa.JpaChannelRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * PackageName  : com.sprint.mission.discodeit.slice
 * FileName     : ChannelRepositoryTest
 * Author       : dounguk
 * Date         : 2025. 6. 21.
 */
@DataJpaTest
@ActiveProfiles("test")
@Import(QuerydslConfig.class)
@DisplayName("Channel Repository 테스트")
public class ChannelRepositoryTest {

    @Autowired
    private JpaChannelRepository channelRepository;

    @Test
    @DisplayName("public 채널이 필요하면 모든 public 채널을 찾아야 한다.")
    void whenRequestPublicChannels_thenFindAllPublicChannels() {
        // given
        int numberOfPublicChannel = 3;
        int numberOfPrivateChannel = 1;

        for (int i = 1; i <= numberOfPublicChannel; i++) {
            Channel channel = Channel.builder()
                .name("public")
                .type(ChannelType.PUBLIC)
                .build();
            channelRepository.save(channel);
        }
        for (int i = 1; i <= numberOfPrivateChannel; i++) {
            Channel channel = Channel.builder()
                .name("private")
                .type(ChannelType.PRIVATE)
                .build();
            channelRepository.save(channel);
        }

        // when
        List<Channel> channels = channelRepository.findAllByType(ChannelType.PUBLIC);

        // then
        assertThat(channels.size()).isEqualTo(numberOfPublicChannel);
        assertThat(channels.get(0).getName()).isEqualTo("public");
        assertThat(channels.get(0).getType()).isEqualTo(ChannelType.PUBLIC);
    }

    @Test
    @DisplayName("private 채널이 필요하면 모든 private 채널을 찾아야 한다.")
    void whenRequestPrivateChannels_thenFindAllPrivateChannels(){
        // given
        int numberOfPublicChannel = 3;
        int numberOfPrivateChannel = 1;

        for (int i = 1; i <= numberOfPublicChannel; i++) {
            Channel channel = Channel.builder()
                .name("public")
                .type(ChannelType.PUBLIC)
                .build();
            channelRepository.save(channel);
        }
        for (int i = 1; i <= numberOfPrivateChannel; i++) {
            Channel channel = Channel.builder()
                .name("private")
                .type(ChannelType.PRIVATE)
                .build();
            channelRepository.save(channel);
        }

        // when
        List<Channel> channels = channelRepository.findAllByType(ChannelType.PRIVATE);

        // then
        assertThat(channels.size()).isEqualTo(numberOfPrivateChannel);
        assertThat(channels.get(0).getName()).isEqualTo("private");
        assertThat(channels.get(0).getType()).isEqualTo(ChannelType.PRIVATE);
    }
}
