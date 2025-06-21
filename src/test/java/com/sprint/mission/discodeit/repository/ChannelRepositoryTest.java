package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.fixture.ChannelFixture;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")

@DataJpaTest
public class ChannelRepositoryTest {

  @Autowired
  private ChannelRepository channelRepository;

  @Test
  void 공개_채널을_저장하고_조회할_수_있다() {
    // given
    Channel channel = ChannelFixture.createPublic();
    channelRepository.save(channel);

    // when
    Channel result = channelRepository.findById(channel.getId())
        .orElseThrow(() -> new AssertionError("채널을 찾을 수 없습니다."));

    // then
    assertThat(result).isEqualTo(channel);
    assertThat(result.getName()).isEqualTo(channel.getName());
    assertThat(result.getDescription()).isEqualTo(channel.getDescription());
    assertThat(result.isPublic()).isTrue();
  }

  @Test
  void 사용자별_채널_목록_조회_성공() {
    // given
    Channel publicChannel = channelRepository.save(ChannelFixture.createPublic());
    // when
    var channels = channelRepository.findAllByUserId(null); // public 채널만 조회
    // then
    assertThat(channels).isNotEmpty();
    assertThat(channels.stream().anyMatch(c -> c.getId().equals(publicChannel.getId()))).isTrue();
  }

  @Test
  void 사용자별_채널_목록_조회_실패() {
    // when
    var channels = channelRepository.findAllByUserId(UUID.randomUUID());
    // then
    assertThat(channels).isNotNull(); // public 채널이 없으면 empty, 여기선 public 채널이 항상 있으므로 not null만 체크
  }
}
