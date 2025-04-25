package com.sprint.mission.discodeit.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.sprint.mission.discodeit.fixture.ChannelFixture;
import com.sprint.mission.discodeit.fixture.MessageFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class MessageTest {

  @Nested
  @DisplayName("메시지 생성")
  class Create {

    @Test
    @DisplayName("메시지가 생성되면 기본 정보가 올바르게 설정되어야 한다")
    void shouldCreateMessageWithDefaultInfo() {
      // given
      Channel channel = ChannelFixture.createDefaultChannel();
      User user = channel.getCreator(); // 채널 생성 시 creator는 기본 참여자가 된다.
      String content = "테스트 메시지입니다?";

      // when
      Message message = MessageFixture.createCustomMessage(content, user, channel);

      // then
      assertAll(
          "메시지 기본 정보 검증",
          () -> assertThat(message.getId()).as("메시지 ID는 null이 아니어야 함").isNotNull(),
          () -> assertThat(message.getContent()).as("메시지 내용이 올바르게 설정되어야 함").isEqualTo(content),
          () -> assertThat(message.getUserId()).as("작성자 ID가 올바르게 설정되어야 함").isEqualTo(user.getId()),
          () -> assertThat(message.getChannelId()).as("채널 ID가 올바르게 설정되어야 함")
              .isEqualTo(channel.getId()),
          () -> assertThat(message.getCreatedAt()).as("생성 시간이 올바르게 설정되어야 함").isNotNull(),
          () -> assertThat(message.getUpdatedAt())
              .as("최초 수정 시간은 생성 시간과 동일해야 함")
              .isEqualTo(message.getCreatedAt())
      );
    }
  }
}
