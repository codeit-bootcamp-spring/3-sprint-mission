package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.io.Serializable;
import java.time.Duration;
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
@Table(name = "user_statuses")
public class UserStatus extends BaseUpdatableEntity implements Serializable {

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
  //
  @OneToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(name = "last_active_at", nullable = false)
  private Instant lastActiveAt;

  /* XXX: status는 DB에 없음 */
  @Transient
  private UserStatusType status;

  public UserStatus(User user) {
    this.status = UserStatusType.ONLINE;
    this.lastActiveAt = Instant.now();
    //
    this.user = user;
  }

//  public void update(UserStatusType newStatus) {
//    boolean anyValueUpdated = false;
//    if (newStatus != null && newStatus != this.status) {
//      // 이전 상태가 온라인이였고 현재가 온라인이 아닐때, 바뀌는 시점에 lastActiveAt를 현재시간으로 업데이트 해줘야함.
//      if (this.status.equals(UserStatusType.ONLINE)) {
//        this.lastActiveAt = Instant.now();
//      }
//
//      this.status = newStatus;
//
//      anyValueUpdated = true;
//    }
//
//    if (anyValueUpdated) {
//      this.updatedAt = Instant.now();
//    }
//  }

  public void update(Instant newLastActiveAt) {
    boolean anyValueUpdated = false;

    if (newLastActiveAt != null && this.lastActiveAt != newLastActiveAt) {
      this.lastActiveAt = newLastActiveAt;
      anyValueUpdated = true;
    }

    /*deprecated field (newStatus) */
//    if (newStatus != null && newStatus != this.status) {
//      // 이전 상태가 온라인이였고 현재가 온라인이 아닐때, 바뀌는 시점에 lastActiveAt를 현재시간으로 업데이트 해줘야함.
//      if (this.status.equals(UserStatusType.ONLINE)) {
//        this.lastActiveAt = Instant.now();
//      }
//
//      this.status = newStatus;
//
//      anyValueUpdated = true;
//    }

    if (anyValueUpdated) {
      this.updatedAt = Instant.now();
    }
  }


  //lastActiveAt 값이 5분 이내라면 온라인 유저로 간주
  public boolean isOnline() {
    Instant instantFiveMinutesAgo = Instant.now().minus(Duration.ofMinutes(5));

    return lastActiveAt.isAfter(instantFiveMinutesAgo);
  }

  @Override
  public String toString() {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        .withZone(ZoneId.systemDefault());

    String createdAtFormatted = (createdAt != null) ? formatter.format(createdAt) : null;
    String updatedAtFormatted = (updatedAt != null) ? formatter.format(updatedAt) : null;

    return "🙋‍♂️ UserStatus {\n" +
        "  id         = " + id + "\n" +
        "  createdAt  = " + createdAtFormatted + "\n" +
        "  updatedAt  = " + updatedAtFormatted + "\n" +
        "  user       = " + user + "\n" +
        "  status       = " + status + "\n" +
        "  lastActiveAt       = " + lastActiveAt + "\n" +
        "}";
  }
}
