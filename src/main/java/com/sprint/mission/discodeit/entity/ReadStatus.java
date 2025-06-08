package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@NoArgsConstructor
@AllArgsConstructor /* @Builder 때문에 넣어줌 */
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "read_statuses")
public class ReadStatus extends BaseUpdatableEntity implements Serializable {

  private static final Long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", nullable = false)
  private UUID id;

  @CreatedDate
  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @LastModifiedDate
  @Column(name = "updated_at")
  private Instant updatedAt;

  @Column(name = "last_read_at", nullable = false)
  private Instant lastReadAt;
  //

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne
  @JoinColumn(name = "channel_id", nullable = false)
  private Channel channel;

  // user, channel 값 다 가져오는데 왜 안들어가지??????????
  // XXX. 처음에 생성될때 읽지않은 상태가 초기값이 맞나? -> YES
  public ReadStatus(User user, Channel channel, Instant lastReadAt) {
    this.user = user;
    this.channel = channel;
    this.lastReadAt = lastReadAt;
  }

  public void update(Instant lastReadAt) {
    boolean anyValueUpdated = false;
    if (lastReadAt != null && !lastReadAt.equals(this.lastReadAt)) {
      this.lastReadAt = lastReadAt;
      anyValueUpdated = true;
    }

    if (anyValueUpdated) {
      this.updatedAt = Instant.now();

    }
  }

  @Override
  public String toString() {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        .withZone(ZoneId.systemDefault());

    String createdAtFormatted = (createdAt != null) ? formatter.format(createdAt) : null;
    String updatedAtFormatted = (updatedAt != null) ? formatter.format(updatedAt) : null;

    return "🙋‍♂️ ReadStatus {\n" +
        "  id         = " + id + "\n" +
        "  createdAt  = " + createdAtFormatted + "\n" +
        "  updatedAt  = " + updatedAtFormatted + "\n" +
        "  user       = " + user + "\n" +
        "  channel       = " + channel + "\n" +
        "  lastReadAt       = " + lastReadAt + "\n" +
        "}";
  }
}
