package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_statuses")
public class UserStatus extends BaseUpdatableEntity {

  private static final long ADDITIONAL_TIME_SECONDS = 60 * 5;

  @OneToOne
  @JoinColumn(name = "user_id", nullable = false, unique = true)
  private User user;

  @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE", nullable = false)
  private Instant lastActiveAt;

  public static UserStatus createUserStatus(User userId) {
    return new UserStatus(userId, Instant.now());
  }

  public UserStatus(User user, Instant lastActiveAt) {
    this.user = user;
    this.lastActiveAt = lastActiveAt;
  }

  public void updateLastActiveAt(Instant newLastActiveAt) {
    this.lastActiveAt = newLastActiveAt;
  }

  public boolean isOnline() {
    Instant validTime = this.lastActiveAt.plusSeconds(ADDITIONAL_TIME_SECONDS);
    return validTime.compareTo(Instant.now()) >= 0;
  }
}
