package com.sprint.mission.discodeit.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.fixture.ChannelFixture;
import com.sprint.mission.discodeit.fixture.MessageFixture;
import com.sprint.mission.discodeit.fixture.UserFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class MessageTest {

  private User user;
  private Channel channel;

  @BeforeEach
  public void setUp() {
    user = UserFixture.createValidUser();
    channel = ChannelFixture.createPublic();
  }

  @Nested
  class Create {

    @Test
    void 메시지가_생성되면_기본_정보가_올바르게_설정되어야_한다() {
      Message message = MessageFixture.createValid();

      assertAll(
          () -> assertThat(message.getId()).isNotNull(),
          () -> assertThat(message.getContent()).isEqualTo(MessageFixture.DEFAULT_MESSAGE_CONTENT),
          () -> assertThat(message.getAuthorId()).isNotNull(),
          () -> assertThat(message.getChannelId()).isNotNull(),
          () -> assertThat(message.getCreatedAt()).isNotNull()
      );
    }

    @Test
    void 다양한_메시지_내용으로_생성해도_올바르게_설정되어야_한다() {
      String specialContent = "특수문자!@#$%^&*() 포함 메시지";
      var message = MessageFixture.createCustom(
          new MessageCreateRequest(specialContent, user.getId(), channel.getId()));

      assertAll(
          () -> assertThat(message.getContent()).isEqualTo(specialContent),
          () -> assertThat(message.getAuthorId()).isEqualTo(user.getId()),
          () -> assertThat(message.getChannelId()).isEqualTo(channel.getId())
      );
    }
  }

  @Nested
  class Read {

    @Test
    void 서로_다른_사용자의_메시지는_독립적이어야_한다() {
      var user1 = UserFixture.createValidUser();
      var user2 = UserFixture.createValidUser();

      var message1 = MessageFixture.createCustom(
          new MessageCreateRequest("우왕굳", user1.getId(), channel.getId()));
      var message2 = MessageFixture.createCustom(
          new MessageCreateRequest("우왕굳", user2.getId(), channel.getId()));

      assertAll(
          () -> assertThat(message1.getId()).isNotEqualTo(message2.getId()),
          () -> assertThat(message1.getAuthorId()).isNotEqualTo(message2.getAuthorId())
      );
    }
  }
}
