package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import java.time.Instant;
import java.util.UUID;

public class ReadStatus extends BaseUpdatableEntity {
  private User user;
  private Channel channel;
  private Message message;

  public ReadStatus(UUID userId, UUID id, Instant createdAt) {
    super();
  }

  public User getUser() { return user; }
  public void setUser(User user) { this.user = user; }
  public Channel getChannel() { return channel; }
  public void setChannel(Channel channel) { this.channel = channel; }
  public Message getMessage() { return message; }
  public void setMessage(Message message) { this.message = message; }

  public UUID getUserId() {
    return user != null ? user.getId() : null;
  }
  public UUID getChannelId() {
    return channel != null ? channel.getId() : null;
  }
  public UUID getMessageId() {
    return message != null ? message.getId() : null;
  }

  public void update(Instant newLastReadAt) {
  }
}
