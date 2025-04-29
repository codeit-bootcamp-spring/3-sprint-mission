package com.sprint.mission.discodeit.fixture;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

/**
 * Channel 테스트를 위한 설비
 */
public class ChannelFixture {

  public static final String DEFAULT_CHANNEL_NAME = "테스트 채널";

  public static Channel createValidChannel() {
    User creator = UserFixture.createValidUser();
    return Channel.create(creator, DEFAULT_CHANNEL_NAME);
  }

  public static Channel createCustomChannel(User creator, String name) {
    return Channel.create(creator, name);
  }
}