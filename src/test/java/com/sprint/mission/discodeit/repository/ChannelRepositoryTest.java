package com.sprint.mission.discodeit.repository;

import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@EnableJpaAuditing
@DataJpaTest
@DisplayName("ChannelRepository 슬라이스 테스트")
public class ChannelRepositoryTest {

  @Autowired
  private ChannelRepository channelRepository;

}
