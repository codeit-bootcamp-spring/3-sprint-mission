package com.sprint.mission.discodeit.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.UserStatusException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_statuses")
public class UserStatus extends BaseUpdatableEntity {

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false, unique = true)
  @JsonIgnore
  private User user;

  @CreatedDate
  @Column(name = "last_active_at", nullable = false)
  private Instant lastActiveAt;

  private static final Duration ACTIVE_DURATION = Duration.ofMinutes(5);

  private UserStatus(User user) {
    if (user == null) {
      throw new UserStatusException(ErrorCode.INVALID_INPUT, "userId는 필수입니다.");
    }
    this.user = user;
    this.lastActiveAt = getCreatedAt();
  }

  public static UserStatus create(User userId) {
    return new UserStatus(userId);
  }

  public void assignIdForTest(UUID id) {
    this.id = id;
  }

  public void updateLastActiveAt() {
    this.lastActiveAt = Instant.now();
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
    return this.lastActiveAt.isAfter(Instant.now().minus(ACTIVE_DURATION));
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