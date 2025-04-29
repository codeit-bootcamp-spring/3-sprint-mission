package com.sprint.mission.discodeit.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.sprint.mission.discodeit.fixture.ChannelFixture;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ReadStatusTest {

  @Nested
  @DisplayName("읽기 상태 생성")
  class Create {

    @Test
    @DisplayName("읽기 상태가 생성되면 기본 정보가 올바르게 설정되어야 한다")
    void shouldCreateReadStatusWithDefaultInfo() {
      // given
      Channel channel = ChannelFixture.createValidChannel();
      User user = channel.getCreator();

      // when
      ReadStatus readStatus = ReadStatus.create(user.getId(), channel.getId());

      // then
      assertAll(
          "읽기 상태 기본 정보 검증",
          () -> assertThat(readStatus.getId()).as("ID는 null이 아니어야 함").isNotNull(),
          () -> assertThat(readStatus.getCreatedAt()).as("생성 시간이 올바르게 설정되어야 함").isNotNull(),
          () -> assertThat(readStatus.getUpdatedAt()).as("최초 수정 시간은 생성 시간과 동일해야 함")
              .isEqualTo(readStatus.getCreatedAt()),
          () -> assertThat(readStatus.getUserId()).as("사용자 ID가 올바르게 설정되어야 함")
              .isEqualTo(user.getId()),
          () -> assertThat(readStatus.getChannelId()).as("채널 ID가 올바르게 설정되어야 함")
              .isEqualTo(channel.getId()),
          () -> assertThat(readStatus.getLastReadAt()).as("마지막 읽은 시간은 생성 시간과 동일해야 함")
              .isEqualTo(readStatus.getCreatedAt())
      );
    }
  }

  @Nested
  @DisplayName("읽기 상태 업데이트")
  class Update {

    @Test
    @DisplayName("updateLastReadAt() 호출 시 lastReadAt과 updatedAt이 동시에 갱신되어야 한다")
    void shouldUpdateBothTimestamps() {
      // given
      Channel channel = ChannelFixture.createValidChannel();
      User user = channel.getCreator();
      ReadStatus readStatus = ReadStatus.create(user.getId(), channel.getId());

      // when
      Instant beforeUpdate = Instant.now();
      readStatus.updateLastReadAt();
      Instant afterUpdate = Instant.now();

      // then
      assertAll(
          () -> assertThat(readStatus.getLastReadAt())
              .as("lastReadAt은 업데이트가 실행된 beforeUpdate, afterUpdate 사이의 시간이어야 함")
              .isBetween(beforeUpdate, afterUpdate),

          () -> assertThat(readStatus.getUpdatedAt())
              .as("updatedAt은 업데이트가 실행된 beforeUpdate, afterUpdate 사이의 시간이어야 함")
              .isBetween(beforeUpdate, afterUpdate),

          () -> assertThat(readStatus.getLastReadAt())
              .as("lastReadAt과 updatedAt은 같다")
              .isEqualTo(readStatus.getUpdatedAt())
      );
    }

    @Test
    @DisplayName("수정 시간 업데이트 메서드가 올바르게 동작해야 한다")
    void shouldUpdateTimestamp() {
      // given
      Channel channel = ChannelFixture.createValidChannel();
      User user = channel.getCreator();
      ReadStatus readStatus = ReadStatus.create(user.getId(), channel.getId());
      Instant originalUpdatedAt = readStatus.getUpdatedAt();

      // when
      readStatus.touch();
      Instant newUpdatedAt = readStatus.getUpdatedAt();

      // then
      assertThat(newUpdatedAt).as("수정 시간이 갱신되어야 함")
          .isAfterOrEqualTo(originalUpdatedAt);
    }
  }
}