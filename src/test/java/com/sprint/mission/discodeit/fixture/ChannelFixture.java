package com.sprint.mission.discodeit.fixture;

import java.util.UUID;

import com.sprint.mission.discodeit.entity.Channel;

public class ChannelFixture {

  public static final String DEFAULT_CHANNEL_NAME = "테스트 채널";
  public static final String DEFAULT_CHANNEL_DESCRIPTION = "테스트 채널쓰임다";

  public static Channel createPublic() {
    return Channel.createPublic(DEFAULT_CHANNEL_NAME,
        DEFAULT_CHANNEL_DESCRIPTION);
  }

  public static Channel createPublic(String name, String description) {
    return Channel.createPublic(name, description);
  }

  public static Channel createPrivate() {
    return Channel.createPrivate();
  }

  public static Channel createValidChannel() {
    return Channel.createPublic(DEFAULT_CHANNEL_NAME, DEFAULT_CHANNEL_DESCRIPTION);
  }

  public static Channel createValidChannelWithId() {
    Channel channel = createValidChannel();
    channel.assignIdForTest(UUID.randomUUID());
    return channel;
  }
}