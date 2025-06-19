package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.fixture.ChannelFixture;
import com.sprint.mission.discodeit.fixture.ReadStatusFixture;
import com.sprint.mission.discodeit.fixture.UserFixture;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DataJpaTest
@EnableJpaAuditing
public class ReadStatusRepositoryTest {

  @Autowired
  TestEntityManager em;

  @Autowired
  private ReadStatusRepository readStatusRepository;


  @Test
  void 공개_채널을_저장하고_조회할_수_있다() {
    // given
    User user = em.persist(UserFixture.createValidUser());
    Channel channel = em.persist(ChannelFixture.createPublic());

    ReadStatus readStatus = ReadStatusFixture.create(user, channel);
    readStatusRepository.save(readStatus);

    // when
    ReadStatus result = readStatusRepository.findById(readStatus.getId())
        .orElseThrow(() -> new AssertionError("읽음 상태를 찾을 수 없습니다."));

    // then
    assertThat(result).isEqualTo(readStatus);
  }
}
