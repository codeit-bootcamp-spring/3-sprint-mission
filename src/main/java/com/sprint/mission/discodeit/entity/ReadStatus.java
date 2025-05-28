package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.Objects;
import lombok.Getter;

/**
 * 사용자가 채널 별 마지막으로 메시지를 읽은 시간을 표현하는 도메인 모델 사용자별 각 채널에 읽지 않은 메시지를 확인하기 위해 활용
 */
@Getter
@Entity
@Table(name = "read_statuses", schema = "discodeit")
public class ReadStatus extends BaseUpdatableEntity {

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne
  @JoinColumn(name = "channel_id", nullable = false)
  private Channel channel;

  @Column(name = "last_read_at", nullable = false)
  private Instant lastReadAt;

  public ReadStatus() {
  }

  public ReadStatus(User user, Channel channel, Instant lastReadAt) {
    this.user = user;
    this.channel = channel;
    this.lastReadAt = lastReadAt;
  }

  public void updateLastReadAt(Instant lastReadAt) {
    this.lastReadAt = lastReadAt;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ReadStatus that = (ReadStatus) o;
    return Objects.equals(user, that.user) && Objects.equals(channel,
        that.channel);
  }

  @Override
  public int hashCode() {
    return Objects.hash(user, channel);
  }
}
