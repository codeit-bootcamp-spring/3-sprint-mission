package com.sprint.mission.discodeit.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
public class UserStatus extends Auditable implements Serializable {

  @Serial
  private static final long serialVersionUID = -7917996053260213133L;

  private static final Duration ACTIVE_DURATION = Duration.ofMinutes(5);

  private final UUID userId;
  private Instant lastActiveAt;

  private UserStatus(UUID userId) {
    this.userId = userId;
    this.lastActiveAt = getCreatedAt();
  }

  public static UserStatus create(UUID userId) {
    UserStatus userStatus = new UserStatus(userId);
    userStatus.touch();
    return userStatus;
  }

  public void updateLastActiveAt() {
    this.lastActiveAt = Instant.now();
    touch();
  }

  public void updateLastActiveAt(Instant instant) {
    this.lastActiveAt = instant;
    this.updatedAt = Instant.now();
  }

  public boolean isOnline() {
    return lastActiveAt.isAfter(Instant.now().minus(ACTIVE_DURATION));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserStatus UserStatus = (UserStatus) o;
    return Objects.equals(getId(), UserStatus.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId());
  }
}