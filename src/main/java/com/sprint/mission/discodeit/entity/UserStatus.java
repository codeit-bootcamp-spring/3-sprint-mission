package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;

@Entity
@Table(name = "user_statuses")
@Getter
public class UserStatus extends BaseUpdatableEntity {

  @Id
  private UUID id;

  @OneToOne
  @JoinColumn(name = "user_id", nullable = false, unique = true)
  private User user;
  
  @Column(name = "last_active_at")
  private Instant lastAccessedAt;

  public UserStatus(User user, Instant lastAccessedAt) {
    this.id = UUID.randomUUID();
    this.user = user;
    this.lastAccessedAt = lastAccessedAt;
  }


  protected UserStatus() {
  }

  public void update(Instant newLastAccessedAt) {
    if (newLastAccessedAt != null && !newLastAccessedAt.equals(this.lastAccessedAt)) {
      this.lastAccessedAt = newLastAccessedAt;
      this.updatedAt = Instant.now();
    }
  }
}
