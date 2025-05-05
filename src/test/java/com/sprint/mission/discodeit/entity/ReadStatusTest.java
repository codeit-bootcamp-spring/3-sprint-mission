package com.sprint.mission.discodeit.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.sprint.mission.discodeit.fixture.ReadStatusFixture;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReadStatus 엔티티 테스트")
public class ReadStatusTest {

  @Nested
  @DisplayName("읽기 상태 생성")
  class Create {

    @Test
    @DisplayName("읽기 상태가 생성되면 기본 정보가 올바르게 설정되어야 한다")
    void shouldCreateReadStatusWithDefaultInfo() {
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
  @DisplayName("읽기 상태 업데이트")
  class Update {

    @Test
    @DisplayName("updateLastReadAt() 호출 시 lastReadAt과 updatedAt이 동시에 갱신되어야 한다")
    void shouldUpdateBothTimestamps() {
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
    @DisplayName("수정 시간 업데이트 메서드가 올바르게 동작해야 한다")
    void shouldUpdateTimestamp() {
      ReadStatus readStatus = ReadStatusFixture.createValidReadStatus();
      Instant originalUpdatedAt = readStatus.getUpdatedAt();

      readStatus.touch();
      Instant newUpdatedAt = readStatus.getUpdatedAt();

      assertThat(newUpdatedAt).as("수정 시간이 갱신되어야 한다")
          .isAfterOrEqualTo(originalUpdatedAt);
    }
  }
}
