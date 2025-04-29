package com.sprint.mission.discodeit.fixture;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

public class MessageFixture {

  public static final String DEFAULT_MESSAGE_CONTENT = "테스트 메시지입니다.";

  public static Message createValidMessage() {
    Channel channel = ChannelFixture.createValidChannel();
    User user = channel.getCreator();

    return Message.create(DEFAULT_MESSAGE_CONTENT, user.getId(), channel.getId());
  }

  public static Message createCustomMessage(String content, User creator, Channel channel) {
    return Message.create(content, creator.getId(), channel.getId());
  }
}
