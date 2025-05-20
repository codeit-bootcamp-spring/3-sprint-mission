package com.sprint.mission.discodeit.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.sprint.mission.discodeit.fixture.ReadStatusFixture;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ReadStatusTest {

  private ReadStatus readStatus;

  @BeforeEach
  void setUp() {
    readStatus = ReadStatusFixture.create();
  }

  @Nested
  class Create {

    @Test
    void 읽기_상태가_생성되면_기본_정보가_올바르게_설정되어야_한다() {

      assertAll(
          () -> assertThat(readStatus.getId()).isNotNull(),
          () -> assertThat(readStatus.getCreatedAt()).isNotNull(),
          () -> assertThat(readStatus.getUserId()).isNotNull(),
          () -> assertThat(readStatus.getChannelId()).isNotNull()
      );
    }
  }

  @Nested
  class Update {

    @Test
    void updateLastReadAt_호출_시_lastReadAt과_updatedAt이_동시에_갱신되어야_한다() {
      Instant beforeUpdate = Instant.now();
      readStatus.updateLastReadAt();
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
