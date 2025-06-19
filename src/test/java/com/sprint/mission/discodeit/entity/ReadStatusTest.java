package com.sprint.mission.discodeit.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.sprint.mission.discodeit.fixture.ChannelFixture;
import com.sprint.mission.discodeit.fixture.ReadStatusFixture;
import com.sprint.mission.discodeit.fixture.UserFixture;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DataJpaTest
@EnableJpaAuditing
public class ReadStatusTest {

  @Autowired
  TestEntityManager em;

  private ReadStatus readStatus;

  @BeforeEach
  void setUp() {
    User user = UserFixture.createValidUser();
    Channel channel = ChannelFixture.createPublic();
    em.persist(user);
    em.persist(channel);

    readStatus = ReadStatusFixture.create(user, channel);
    em.persist(readStatus);
    em.flush();
  }

  @Nested
  class Create {

    @Test
    void 읽기_상태가_생성되면_기본_정보가_올바르게_설정되어야_한다() {

      assertAll(
          () -> assertThat(readStatus.getId()).isNotNull(),
          () -> assertThat(readStatus.getCreatedAt()).isNotNull(),
          () -> assertThat(readStatus.getUser()).isNotNull(),
          () -> assertThat(readStatus.getChannel()).isNotNull()
      );
    }
  }

  @Nested
  class Update {

    @Test
    void updateLastReadAt_호출_시_lastReadAt과_updatedAt이_동시에_갱신되어야_한다() {
      Instant beforeUpdate = Instant.now();

      readStatus.updateLastReadAt();

      em.persist(readStatus);
      em.flush();
      em.clear();

      Instant afterUpdate = Instant.now();

      assertAll(
          () -> assertThat(readStatus.getLastReadAt())
              .as("lastReadAt은 업데이트 시점 사이의 시간이어야 한다")
              .isBetween(beforeUpdate, afterUpdate),
          () -> assertThat(readStatus.getUpdatedAt())
              .as("updatedAt은 업데이트 시점 사이의 시간이어야 한다")
              .isBetween(beforeUpdate, afterUpdate)
      );
    }
  }
}
