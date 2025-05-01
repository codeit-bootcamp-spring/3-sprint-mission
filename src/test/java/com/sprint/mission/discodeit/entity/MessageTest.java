package com.sprint.mission.discodeit.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.sprint.mission.discodeit.fixture.MessageFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Message 엔티티 테스트")
public class MessageTest {

  @Nested
  @DisplayName("메시지 생성")
  class Create {

    @Test
    @DisplayName("메시지가 생성되면 기본 정보가 올바르게 설정되어야 한다")
    void shouldCreateMessageWithDefaultInfo() {
      Message message = MessageFixture.createValidMessage();

      assertAll(
          () -> assertThat(message.getId()).isNotNull(),
          () -> assertThat(message.getContent()).isEqualTo(MessageFixture.DEFAULT_MESSAGE_CONTENT),
          () -> assertThat(message.getUserId()).isNotNull(),
          () -> assertThat(message.getChannelId()).isNotNull(),
          () -> assertThat(message.getCreatedAt()).isNotNull(),
          () -> assertThat(message.getUpdatedAt()).isEqualTo(message.getCreatedAt())
      );
    }

    @Test
    @DisplayName("다양한 메시지 내용으로 생성해도 올바르게 설정되어야 한다")
    void shouldCreateMessageWithVariousContents() {
      String specialContent = "특수문자!@#$%^&*() 포함 메시지";
      var messagePack = MessageFixture.createValidMessageWithCustomContent(specialContent);

      Message message = messagePack.message();

      assertAll(
          () -> assertThat(message.getContent()).isEqualTo(specialContent),
          () -> assertThat(message.getUserId()).isEqualTo(messagePack.user().getId()),
          () -> assertThat(message.getChannelId()).isEqualTo(messagePack.channel().getId())
      );
    }
  }

  @Nested
  @DisplayName("메시지 속성 검증")
  class Properties {

    @Test
    @DisplayName("서로 다른 사용자의 메시지는 독립적이어야 한다")
    void messagesShouldBeIndependent() {
      var messagePack1 = MessageFixture.createValidMessageWithCustomContent("메시지 1");
      var messagePack2 = MessageFixture.createValidMessageWithCustomContent("메시지 2");

      Message message1 = messagePack1.message();
      Message message2 = messagePack2.message();

      assertAll(
          () -> assertThat(message1.getId()).isNotEqualTo(message2.getId()),
          () -> assertThat(message1.getUserId()).isNotEqualTo(message2.getUserId()),
          () -> assertThat(message1.getContent()).isNotEqualTo(message2.getContent())
      );
    }
  }
}
