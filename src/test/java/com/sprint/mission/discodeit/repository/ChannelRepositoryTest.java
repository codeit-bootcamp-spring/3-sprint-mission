package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.sprint.mission.discodeit.config.TestJpaConfig;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@Import(TestJpaConfig.class)
public class ChannelRepositoryTest {

    @Autowired
    ChannelRepository channelRepository;

    @Test
    @DisplayName("채널을 저장한 뒤, ID로 조회할 수 있다")
    void save_then_findById_should_return_channel() {
        // Given
        Channel channel = new Channel(ChannelType.PUBLIC, "일반채널", "채널 설명");
        Channel saved = channelRepository.save(channel);

        // When
        Optional<Channel> result = channelRepository.findById(saved.getId());

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("일반채널");
        assertThat(result.get().getType()).isEqualTo(ChannelType.PUBLIC);
        assertThat(result.get().getDescription()).isEqualTo("채널 설명");
    }

    @Nested
    @DisplayName("사용자 별 채널 목록을 조회할 수 있다")
    class FindAllByTypeOrIdInTests {

        @Test
        @DisplayName("사용자 별 채널 목록 조회 성공")
        void findAll_by_type_or_id_in_should_return_channels() {
            // Given
            Channel public1 = new Channel(ChannelType.PUBLIC, "공개 채널1", "오메가");
            Channel public2 = new Channel(ChannelType.PUBLIC, "공개 채널2", "롤렉스");
            Channel private1 = new Channel(ChannelType.PRIVATE, "비공개 채널1", null);
            Channel private2 = new Channel(ChannelType.PRIVATE, "비공개 채널2", null);

            channelRepository.saveAll(List.of(public1, public2, private1, private2));

            // When
            List<Channel> result = channelRepository.findAllByTypeOrIdIn(
                ChannelType.PUBLIC,
                List.of(private1.getId())
            );

            // Then
            assertThat(result.size()).isEqualTo(3);
            assertThat(result.containsAll(List.of(public1, public2, private1))).isTrue();

        }

        @Test
        @DisplayName("조회 조건을 만족하는 채널이 없으면 빈 리스트를 반환한다")
        void findAll_by_type_or_id_in_should_return_empty_list() {
            // Given
            Channel private1 = new Channel(ChannelType.PRIVATE, "비공개 채널1", null);
            Channel private2 = new Channel(ChannelType.PRIVATE, "비공개 채널2", null);

            channelRepository.saveAll(List.of(private1, private2));

            // When
            List<Channel> result = channelRepository.findAllByTypeOrIdIn(
                ChannelType.PUBLIC,
                List.of()
            );

            // Then
            assertThat(result.isEmpty()).isTrue();
        }

    }


}
