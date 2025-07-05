package com.sprint.mission.discodeit.entity;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "read_statuses", uniqueConstraints = @UniqueConstraint(columnNames = { "user_id", "channel_id" }))
public class ReadStatus extends BaseUpdatableEntity {

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "channel_id", nullable = false)
  private Channel channel;

  @Column(name = "last_read_at", nullable = false)
  private Instant lastReadAt;

  private ReadStatus(User user, Channel channel) {
    this.user = user;
    this.channel = channel;
    this.lastReadAt = Instant.now();
  }

  public static ReadStatus create(User user, Channel channel) {
    return new ReadStatus(user, channel);
  }

  public void updateLastReadAt() {
    this.lastReadAt = Instant.now();
  }

  public void assignIdForTest(UUID id) {
    this.id = id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ReadStatus readStatus)) {
      return false;
    }

    return Objects.equals(id, readStatus.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}