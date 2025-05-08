package com.sprint.mission.discodeit.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.sprint.mission.discodeit.fixture.MessageFixture;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class MessageTest {

  @Nested
  class Create {

    @Test
    void 메시지가_생성되면_기본_정보가_올바르게_설정되어야_한다() {
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
    void 다양한_메시지_내용으로_생성해도_올바르게_설정되어야_한다() {
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
  class Read {

    @Test
    void 서로_다른_사용자의_메시지는_독립적이어야_한다() {
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
