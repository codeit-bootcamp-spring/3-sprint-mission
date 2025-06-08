package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.common.Constants;
import com.sprint.mission.discodeit.entity.base.BaseUpdateableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.Instant;

@Entity
@Table(name = "user_statuses")
@Getter
@NoArgsConstructor
public class UserStatus extends BaseUpdateableEntity {

  @OneToOne // User-UserStatus = 1:1
  @JoinColumn(name = "user_id", nullable = false, unique = true) // Foreign Key + Unique
  private User user;

  @Column(name = "last_active_at", nullable = false)
  private Instant lastActiveAt;

  public UserStatus(User user, Instant lastActiveAt) {
    this.user = user;
    this.lastActiveAt = lastActiveAt;
  }

  public void update(Instant lastActiveAt) {
    if (lastActiveAt != null && !lastActiveAt.equals(this.lastActiveAt)) {
      this.lastActiveAt = lastActiveAt;
    }
  }

  public Boolean isOnline() {
    Instant thresholdTime = Instant.now().minus(Duration.ofMinutes(Constants.UserStatus.ONLINE_THRESHOLD_MINUTES));
    return lastActiveAt.isAfter(thresholdTime);
  }
}
