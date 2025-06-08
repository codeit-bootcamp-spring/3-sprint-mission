package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdateableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "read_statuses", uniqueConstraints = @UniqueConstraint(name = "uk_read_status_user_channel", columnNames = {
    "user_id", "channel_id" })) // 유니크 제약조건
@Getter
@NoArgsConstructor
public class ReadStatus extends BaseUpdateableEntity {

  @ManyToOne // ReadStatus-User = N:1
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne // ReadStatus-Channel = N:1
  @JoinColumn(name = "channel_id", nullable = false)
  private Channel channel;

  @Column(name = "last_read_at", nullable = false)
  private Instant lastReadAt;

  public ReadStatus(User user, Channel channel, Instant lastReadAt) {
    this.user = user;
    this.channel = channel;
    this.lastReadAt = lastReadAt;
  }

  public void update(Instant newLastReadAt) {
    if (newLastReadAt != null && !newLastReadAt.equals(this.lastReadAt)) {
      this.lastReadAt = newLastReadAt;
    }
  }
}
