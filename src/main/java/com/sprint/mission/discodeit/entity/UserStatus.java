package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.UserStatusException;
import java.io.Serial;
import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class UserStatus implements Serializable {

  @Serial
  private static final long serialVersionUID = -7917996053260213133L;

  private static final Duration ACTIVE_DURATION = Duration.ofMinutes(5);

  private final UUID id;
  private final Instant createdAt;
  private Instant updatedAt;

  private final UUID userId;
  private Instant lastActiveAt;

  private UserStatus(UUID userId) {
    if (userId == null) {
      throw new UserStatusException(ErrorCode.INVALID_INPUT, "userId는 필수입니다.");
    }
    this.id = UUID.randomUUID();
    this.createdAt = Instant.now();
    this.userId = userId;
    this.lastActiveAt = getCreatedAt();
  }

  public static UserStatus create(UUID userId) {
    UserStatus userStatus = new UserStatus(userId);
    userStatus.touch();
    return userStatus;
  }

  public void touch() {
    this.updatedAt = Instant.now();
  }

  public void updateLastActiveAt() {
    this.lastActiveAt = Instant.now();
    touch();
  }

  public void updateLastActiveAt(Instant instant) {
    if (instant == null) {
      throw new UserStatusException(ErrorCode.INVALID_INPUT, "lastActiveAt는 필수입니다.");
    }
    this.lastActiveAt = instant;
    this.updatedAt = Instant.now();
  }

  public boolean isOnline() {
    if (lastActiveAt == null) {
      throw new UserStatusException(ErrorCode.INVALID_INPUT,
          "lastActiveAt 값이 null이므로 isOnline을 판단할 수 없습니다.");
    }
    return lastActiveAt.isAfter(Instant.now().minus(ACTIVE_DURATION));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof UserStatus userStatus)) {
      return false;
    }
    return Objects.equals(id, userStatus.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}