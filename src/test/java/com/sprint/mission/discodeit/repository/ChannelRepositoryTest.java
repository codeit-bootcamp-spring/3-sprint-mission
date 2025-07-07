package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.config.TestJpaConfig;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@Import(TestJpaConfig.class)
@DisplayName("ChannelRepository 슬라이스 테스트")
public class ChannelRepositoryTest {

    @Autowired private UserRepository userRepository;
    @Autowired private ChannelRepository channelRepository;
    @Autowired private ReadStatusRepository readStatusRepository;
    @Autowired private TestEntityManager em;

    private User user;
    private UUID userId;

    private Channel publicChannel;
    private Channel privateChannelAccessible;

    @BeforeEach
    void setUp() {
        user = userRepository.save(new User("tom", "tom@test.com", "pw123456", null));
        userId = user.getId();

        publicChannel = new Channel(ChannelType.PUBLIC, "public", "public channel");
        channelRepository.save(publicChannel);

        privateChannelAccessible = channelRepository.save(new Channel(ChannelType.PRIVATE, null, null));
        readStatusRepository.save(new ReadStatus(user, privateChannelAccessible, privateChannelAccessible.getCreatedAt()));


        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("채널 id 값으로 조회 성공")
    void findById() {
        // when
        Optional<Channel> optionalChannel = channelRepository.findById(publicChannel.getId());

        // then
        assertThat(optionalChannel).isPresent()
            .get()
            .satisfies(c -> {
                assertThat(c.getId()).isEqualTo(publicChannel.getId());
                assertThat(c.getType()).isEqualTo(ChannelType.PUBLIC);
                assertThat(c.getName()).isEqualTo("public");
                assertThat(c.getDescription()).isEqualTo("public channel");
            });
    }

    @Test
    @DisplayName("존재하지 않는 채널 id 값으로 조회 실패")
    void findById_notFound() {
        UUID randomId = UUID.randomUUID();

        // when
        Optional<Channel> optionalChannel = channelRepository.findById(randomId);

        // then
        assertThat(optionalChannel).isEmpty();
    }

    @Test
    @DisplayName("사용자가 속한 공개/비공개 채널 반환 성공")
    void findAllAccessible() {
        // when
        List<Channel> result = channelRepository.findAllAccessible(ChannelType.PUBLIC, userId);

        // then
        assertThat(result)
            .hasSize(2)
            .extracting(Channel::getId)
            .containsExactlyInAnyOrder(
                publicChannel.getId(),
                privateChannelAccessible.getId()
            );
    }

}