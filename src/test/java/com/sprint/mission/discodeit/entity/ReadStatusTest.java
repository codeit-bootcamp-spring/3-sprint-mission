package com.sprint.mission.discodeit.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.sprint.mission.discodeit.fixture.ReadStatusFixture;
import java.time.Instant;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ReadStatusTest {

  @Nested
  class Create {

    @Test
    void 읽기_상태가_생성되면_기본_정보가_올바르게_설정되어야_한다() {
      ReadStatus readStatus = ReadStatusFixture.createValidReadStatus();

      assertAll(
          () -> assertThat(readStatus.getId()).isNotNull(),
          () -> assertThat(readStatus.getCreatedAt()).isNotNull(),
          () -> assertThat(readStatus.getUpdatedAt()).isEqualTo(readStatus.getCreatedAt()),
          () -> assertThat(readStatus.getUserId()).isNotNull(),
          () -> assertThat(readStatus.getChannelId()).isNotNull(),
          () -> assertThat(readStatus.getLastReadAt()).isEqualTo(readStatus.getCreatedAt())
      );
    }
  }

  @Nested
  class Update {

    @Test
    void updateLastReadAt_호출_시_lastReadAt과_updatedAt이_동시에_갱신되어야_한다() {
      ReadStatus readStatus = ReadStatusFixture.createValidReadStatus();

      Instant beforeUpdate = Instant.now();
      readStatus.updateLastReadAt();
      Instant afterUpdate = Instant.now();

      assertAll(
          () -> assertThat(readStatus.getLastReadAt())
              .as("lastReadAt은 업데이트 시점 사이의 시간이어야 한다")
              .isBetween(beforeUpdate, afterUpdate),
          () -> assertThat(readStatus.getUpdatedAt())
              .as("updatedAt은 업데이트 시점 사이의 시간이어야 한다")
              .isBetween(beforeUpdate, afterUpdate),
          () -> assertThat(readStatus.getLastReadAt())
              .as("lastReadAt과 updatedAt은 동일해야 한다")
              .isEqualTo(readStatus.getUpdatedAt())
      );
    }

    @Test
    void 수정_시간_업데이트_메서드가_올바르게_동작해야_한다() {
      ReadStatus readStatus = ReadStatusFixture.createValidReadStatus();
      Instant originalUpdatedAt = readStatus.getUpdatedAt();

      readStatus.touch();
      Instant newUpdatedAt = readStatus.getUpdatedAt();

      assertThat(newUpdatedAt).as("수정 시간이 갱신되어야 한다")
          .isAfterOrEqualTo(originalUpdatedAt);
    }
  }
}
