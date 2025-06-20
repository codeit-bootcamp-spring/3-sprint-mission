package com.sprint.mission.discodeit.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.Duration;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString(doNotUseGetters = true, callSuper = true)
@Getter
@NoArgsConstructor
@AllArgsConstructor /* @Builder 때문에 넣어줌 */
@Builder
@Entity
@Table(name = "user_statuses")
public class UserStatus extends BaseUpdatableEntity {

  @JsonBackReference
  @OneToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false, unique = true)
  private User user;

  @Column(name = "last_active_at", nullable = false)
  private Instant lastActiveAt;

  /* XXX: status는 DB에 없음 */
  @Transient
  private UserStatusType status;

  public UserStatus(User user, Instant lastActiveAt) {
    this.status = UserStatusType.ONLINE;
    this.lastActiveAt = lastActiveAt;
    //
    this.user = user;
    // user 엔티티에도 연관관계 추가(userStatus가 FK를 가지고 있으므로 관계 설정도 이곳에서 함)
    user.setStatus(this);
  }

  public void update(Instant lastActiveAt) {
    if (lastActiveAt != null && !lastActiveAt.equals(this.lastActiveAt)) {
      this.lastActiveAt = lastActiveAt;
    }
  }

  public boolean isOnline() {
    Instant instantFiveMinutesAgo = Instant.now().minus(Duration.ofMinutes(5));

    return lastActiveAt.isAfter(instantFiveMinutesAgo);
  }

}
