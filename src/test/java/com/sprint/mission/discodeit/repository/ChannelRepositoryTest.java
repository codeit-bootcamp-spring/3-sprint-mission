package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.config.TestJpaAuditConfig;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@Import(TestJpaAuditConfig.class)
@ActiveProfiles("test")
@DisplayName("ChannelRepository 슬라이스 테스트")
public class ChannelRepositoryTest {

    @Autowired
    private ChannelRepository channelRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ReadStatusRepository readStatusRepository;

    private Channel publicChannel;
    private Channel privateChannel;
    private User user;
    private ReadStatus readStatus;

    @BeforeEach
    void setUp() {
        publicChannel = new Channel(ChannelType.PUBLIC, "공개 채널 테스트", "테스트 채널입니다.");
        privateChannel = new Channel(ChannelType.PRIVATE, null, null);
        user = new User("테스트유저", "test@codeit.com", "test1234", null, null);
        readStatus = new ReadStatus(user, privateChannel, Instant.now());

        channelRepository.save(publicChannel);
        channelRepository.save(privateChannel);
        userRepository.save(user);
        readStatusRepository.save(readStatus);
    }

    @Test
    @DisplayName("유저ID로 해당 유저가 조회할 수 있는 모든 채널을 조회할 수 있다.")
    void shouldReturnAllAccessibleChannels_whenGivenValidUserId() {

        // when
        List<Channel> channels = channelRepository.findAllPublicOrUserChannels(user.getId());

        // then
        assertThat(channels).hasSize(2);
        assertThat(channels)
                .extracting("type")
                .containsExactlyInAnyOrder(ChannelType.PUBLIC, ChannelType.PRIVATE);
    }

    @Test
    @DisplayName("존재하지 않는 유저ID로 조회시 공개 채널만 반환된다.")
    void shouldReturnOnlyPublicChannels_whenUserIdIsInvalid() {

        // when
        List<Channel> channels = channelRepository.findAllPublicOrUserChannels(UUID.randomUUID());

        // then
        assertThat(channels).hasSize(1);
        assertThat(channels.get(0).getType()).isEqualTo(ChannelType.PUBLIC);
    }
}
