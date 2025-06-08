package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import java.util.List;
import java.util.UUID;

public class Message extends BaseUpdatableEntity {
  public Channel channel;
  private User user;

  // getter/setter 생략...

  public UUID getChannelId() {
    return channel != null ? channel.getId() : null;
  }
  public UUID getUserId() {
    return user != null ? user.getId() : null;
  }
}
