package com.sprint.mission.discodeit.repository;


import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@EnableJpaAuditing
@DisplayName("ChannelRepository 테스트")
class ChannelRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ChannelRepository channelRepository;

    private Channel publicChannel1;
    private Channel publicChannel2;
    private Channel privateChannel1;
    private Channel privateChannel2;

    @BeforeEach
    void setUp() {
        publicChannel1 = new Channel(ChannelType.PUBLIC, "Public Channel 1",
            "Public channel description 1");
        publicChannel2 = new Channel(ChannelType.PUBLIC, "Public Channel 2",
            "Public channel description 2");
        privateChannel1 = new Channel(ChannelType.PRIVATE, "Private Channel 1",
            "Private channel description 1");
        privateChannel2 = new Channel(ChannelType.PRIVATE, "Private Channel 2",
            "Private channel description 2");

        entityManager.persistAndFlush(publicChannel1);
        entityManager.persistAndFlush(publicChannel2);
        entityManager.persistAndFlush(privateChannel1);
        entityManager.persistAndFlush(privateChannel2);
    }

    @Test
    @DisplayName("타입 또는 ID 목록으로 채널 조회 - 성공 (타입으로 조회)")
    void findAllByTypeOrIdIn_ByType_Success() {
        // given
        List<UUID> emptyIds = Arrays.asList();

        // when
        List<Channel> result = channelRepository.findAllByTypeOrIdIn(ChannelType.PUBLIC, emptyIds);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Channel::getName)
            .contains("Public Channel 1", "Public Channel 2");
        assertThat(result).allMatch(channel -> channel.getType() == ChannelType.PUBLIC);
    }

    @Test
    @DisplayName("타입 또는 ID 목록으로 채널 조회 - 성공 (ID 목록으로 조회)")
    void findAllByTypeOrIdIn_ByIds_Success() {
        // given
        List<UUID> channelIds = Arrays.asList(
            privateChannel1.getId(),
            privateChannel2.getId()
        );

        // when
        List<Channel> result = channelRepository.findAllByTypeOrIdIn(ChannelType.PUBLIC,
            channelIds);

        // then
        assertThat(result).hasSize(4); // PUBLIC 채널 2개 + 지정된 ID 채널 2개
        assertThat(result).extracting(Channel::getName)
            .contains(
                "Public Channel 1",
                "Public Channel 2",
                "Private Channel 1",
                "Private Channel 2"
            );
    }

    @Test
    @DisplayName("타입 또는 ID 목록으로 채널 조회 - 성공 (특정 ID만 조회)")
    void findAllByTypeOrIdIn_SpecificIds_Success() {
        // given
        List<UUID> channelIds = Arrays.asList(privateChannel1.getId());

        // when
        List<Channel> result = channelRepository.findAllByTypeOrIdIn(ChannelType.PRIVATE,
            channelIds);

        // then
        assertThat(result).hasSize(2); // PRIVATE 채널 2개
        assertThat(result).extracting(Channel::getName)
            .contains("Private Channel 1", "Private Channel 2");
    }

    @Test
    @DisplayName("타입 또는 ID 목록으로 채널 조회 - 실패 (존재하지 않는 타입과 ID)")
    void findAllByTypeOrIdIn_NotFound() {
        // given
        List<UUID> nonExistentIds = Arrays.asList(UUID.randomUUID());

        // when
        List<Channel> result = channelRepository.findAllByTypeOrIdIn(null, nonExistentIds);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("타입 또는 ID 목록으로 채널 조회 - 빈 결과 (빈 조건)")
    void findAllByTypeOrIdIn_EmptyConditions() {
        // given
        List<UUID> emptyIds = Arrays.asList();

        // when - null 타입과 빈 ID 목록
        List<Channel> result = channelRepository.findAllByTypeOrIdIn(null, emptyIds);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("타입 또는 ID 목록으로 채널 조회 - 중복 제거 확인")
    void findAllByTypeOrIdIn_DuplicateRemoval() {
        // given - PUBLIC 채널의 ID를 명시적으로 포함
        List<UUID> channelIds = Arrays.asList(
            publicChannel1.getId(),
            publicChannel2.getId()
        );

        // when - PUBLIC 타입과 PUBLIC 채널 ID들을 모두 조건에 포함
        List<Channel> result = channelRepository.findAllByTypeOrIdIn(ChannelType.PUBLIC,
            channelIds);

        // then - 중복이 제거되어 2개만 반환되어야 함
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Channel::getName)
            .contains("Public Channel 1", "Public Channel 2");
    }
}
