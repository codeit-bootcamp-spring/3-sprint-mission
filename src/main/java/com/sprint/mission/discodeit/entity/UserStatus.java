package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import java.time.Instant;
import java.util.UUID;

public class UserStatus extends BaseUpdatableEntity {
  private User user;
  private Instant lastActiveAt;

  public UserStatus(UUID id, Instant now) {
    super();
  }

  public User getUser() { return user; }
  public void setUser(User user) { this.user = user; }
  public Instant getLastActiveAt() { return lastActiveAt; }
  public void setLastActiveAt(Instant lastActiveAt) { this.lastActiveAt = lastActiveAt; }

  public UUID getUserId() {
    return user != null ? user.getId() : null;
  }
}