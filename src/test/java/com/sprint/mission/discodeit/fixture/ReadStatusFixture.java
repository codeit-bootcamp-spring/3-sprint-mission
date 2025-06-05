package com.sprint.mission.discodeit.fixture;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;

public class ReadStatusFixture {

  public static ReadStatus create() {
    User user = UserFixture.createValidUser();
    Channel channel = ChannelFixture.createPrivate();
    return ReadStatus.create(user, channel);
  }

  public static ReadStatus create(User user, Channel channel) {
    return ReadStatus.create(user, channel);
  }
}
