package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.config.JpaAuditingConfig;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.fixture.ChannelFixture;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DataJpaTest
@Import(JpaAuditingConfig.class)
public class ChannelRepositoryTest {

  @Autowired
  TestEntityManager em;

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
}
