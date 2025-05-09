package com.sprint.mission.discodeit.fixture;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import java.util.UUID;

public class MessageFixture {

  public static final String DEFAULT_MESSAGE_CONTENT = "테스트 메시지입니다.";

  public static Message createValidMessage() {
    User user = UserFixture.createValidUser();
    Channel channel = ChannelFixture.createPublic();
    return Message.create(DEFAULT_MESSAGE_CONTENT, user.getId(), channel.getId());
  }

  public static Message createCustomMessage(String content, UUID userId, Channel channel) {
    return Message.create(content, userId, channel.getId());
  }

  public static MessagePack createValidMessageWithCustomContent(String content) {
    User user = UserFixture.createValidUser();
    Channel channel = ChannelFixture.createPublic();
    Message message = Message.create(content, user.getId(), channel.getId());
    return new MessagePack(message, user, channel);
  }

  public static record MessagePack(Message message, User user, Channel channel) {

  }
}
