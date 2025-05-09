package com.sprint.mission.discodeit.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;

@Getter
public class UserStatus implements Serializable {

  @Serial
  private static final long serialVersionUID = 1033046071710410532L;

  private final UUID id;
  private final Instant createdAt;
  private Instant updatedAt;
  private final UUID userId;
  private Instant lastAccessedAt;

  public UserStatus(UUID userId, Instant lastAccessedAt) {
    this.id = UUID.randomUUID();
    this.createdAt = Instant.now();

    this.userId = userId;
    this.lastAccessedAt = lastAccessedAt;
  }

  public void update(Instant lastAccessedAt) {
    boolean anyValueUpdated = false;
    if (lastAccessedAt != null && !lastAccessedAt.equals(this.lastAccessedAt)) {
      this.lastAccessedAt = lastAccessedAt;
      anyValueUpdated = true;
    }

    if (anyValueUpdated) {
      this.updatedAt = Instant.now();
    }
  }

  public boolean isOnline() {
    return Instant.now().minusSeconds(300).isBefore(lastAccessedAt);
  }


}
