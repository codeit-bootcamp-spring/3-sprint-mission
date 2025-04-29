package com.sprint.mission.discodeit.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.sprint.mission.discodeit.fixture.ChannelFixture;
import com.sprint.mission.discodeit.fixture.MessageFixture;
import com.sprint.mission.discodeit.fixture.UserFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class MessageTest {

  private Channel channel;
  private User user;
  private String messageContent;

  @BeforeEach
  void setUp() {
    // 테스트 데이터 초기화로 독립성 보장
    channel = ChannelFixture.createValidChannel();
    user = channel.getCreator(); // 채널 생성 시 creator는 기본 참여자가 된다.
    messageContent = "테스트 메시지입니다";
  }

  @Nested
  @DisplayName("메시지 생성")
  class Create {

    @Test
    @DisplayName("메시지가 생성되면 기본 정보가 올바르게 설정되어야 한다")
    void shouldCreateMessageWithDefaultInfo() {
      // when
      Message message = MessageFixture.createCustomMessage(messageContent, user, channel);

      // then
      assertAll(
          "메시지 기본 정보 검증",
          () -> assertThat(message.getId()).as("메시지 ID는 null이 아니어야 함").isNotNull(),
          () -> assertThat(message.getContent()).as("메시지 내용이 올바르게 설정되어야 함")
              .isEqualTo(messageContent),
          () -> assertThat(message.getUserId()).as("작성자 ID가 올바르게 설정되어야 함").isEqualTo(user.getId()),
          () -> assertThat(message.getChannelId()).as("채널 ID가 올바르게 설정되어야 함")
              .isEqualTo(channel.getId()),
          () -> assertThat(message.getCreatedAt()).as("생성 시간이 올바르게 설정되어야 함").isNotNull(),
          () -> assertThat(message.getUpdatedAt())
              .as("최초 수정 시간은 생성 시간과 동일해야 함")
              .isEqualTo(message.getCreatedAt())
      );
    }

    @Test
    @DisplayName("다양한 메시지 내용으로 생성해도 올바르게 설정되어야 한다")
    void shouldCreateMessageWithVariousContents() {
      // given
      String specialContent = "특수문자!@#$%^&*()도 포함된 메시지";

      // when
      Message message = MessageFixture.createCustomMessage(specialContent, user, channel);

      // then
      assertThat(message.getContent()).as("특수문자가 포함된 메시지 내용이 올바르게 설정되어야 함")
          .isEqualTo(specialContent);
    }
  }

  @Nested
  @DisplayName("메시지 속성 검증")
  class Properties {

    @Test
    @DisplayName("다른 사용자의 메시지는 서로 독립적이어야 한다")
    void messagesShouldBeIndependent() {
      // given
      User anotherUser = UserFixture.createValidUser();
      channel.addParticipant(anotherUser);
      String anotherContent = "다른 사용자의 메시지";

      // when
      Message message1 = MessageFixture.createCustomMessage(messageContent, user, channel);
      Message message2 = MessageFixture.createCustomMessage(anotherContent, anotherUser, channel);

      // then
      assertAll(
          "서로 다른 두 메시지 검증",
          () -> assertThat(message1.getId()).as("메시지 ID는 서로 달라야 함").isNotEqualTo(message2.getId()),
          () -> assertThat(message1.getUserId()).as("작성자 ID가 서로 달라야 함")
              .isNotEqualTo(message2.getUserId()),
          () -> assertThat(message1.getContent()).as("메시지 내용이 서로 달라야 함")
              .isNotEqualTo(message2.getContent())
      );
    }
  }
}