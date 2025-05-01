package com.sprint.mission.discodeit.fixture;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import java.util.UUID;

public class MessageFixture {

  public static final String DEFAULT_MESSAGE_CONTENT = "테스트 메시지입니다.";

  /**
   * 기본 메시지를 생성한다. 채널과 유저도 함께 생성되며, 유저는 채널의 참여자로 추가된다.
   */
  public static Message createValidMessage() {
    User user = UserFixture.createValidUser();
    Channel channel = ChannelFixture.createCustomChannelWithParticipant(
        user, ChannelFixture.DEFAULT_CHANNEL_NAME, ChannelFixture.DEFAULT_CHANNEL_DESCRIPTION);
    return Message.create(DEFAULT_MESSAGE_CONTENT, user.getId(), channel.getId());
  }

  /**
   * 커스텀 메시지를 생성한다. 채널에 유저가 반드시 참여자로 등록되어 있어야 한다.
   */
  public static Message createCustomMessage(String content, UUID userId, Channel channel) {
    if (!channel.isParticipant(userId)) {
      channel.addParticipant(userId);
    }
    return Message.create(content, userId, channel.getId());
  }

  /**
   * 커스텀 메시지 + 유저 + 채널을 함께 반환한다 (유연한 테스트를 위한 구조)
   */
  public static MessagePack createValidMessageWithCustomContent(String content) {
    User user = UserFixture.createValidUser();
    Channel channel = ChannelFixture.createCustomChannelWithParticipant(
        user, ChannelFixture.DEFAULT_CHANNEL_NAME, ChannelFixture.DEFAULT_CHANNEL_DESCRIPTION);
    Message message = Message.create(content, user.getId(), channel.getId());
    return new MessagePack(message, user, channel);
  }

  /**
   * 메시지, 유저, 채널을 묶어서 반환하는 레코드
   */
  public static record MessagePack(Message message, User user, Channel channel) {

  }
}
