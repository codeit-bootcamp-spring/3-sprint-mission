package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import lombok.Getter;

/**
 * 사용자 별 마지막으로 확인된 접속 시간을 표현하는 도메인 모델 사용자의 온라인 상태를 확인하기 위해 활용
 */
@Getter
@Entity
@Table(name = "user_statuses", schema = "discodeit")
public class UserStatus extends BaseUpdatableEntity {

  @OneToOne
  @JoinColumn(name = "user_id")
  private User user;

  @Column(name = "last_active_at")
  private Instant lastActiveAt;

  private static final long LOGIN_TIMEOUT_MINUTES = 5L;

  public UserStatus() {
  }

  public UserStatus(User user, Instant lastActiveAt) {
    this.user = user;
    this.lastActiveAt = lastActiveAt;
  }

  public void updatelastActiveAt(Instant lastActiveAt) {
    this.lastActiveAt = lastActiveAt;
  }

  /**
   * 마지막 접속 시간을 기준으로 현재 로그인한 유저로 판단할 수 있는 메소드
   *
   * @return 마지막 접속 시간이 현재 시간으로부터 5분 이내인지
   */
  public boolean isLogin() {
    if (lastActiveAt == null) {
      return false;
    }

    Instant now = Instant.now();

    // lastActiveAt이 현재 시간보다 미래일 수는 없기 때문에 false 반환
    if (lastActiveAt.isAfter(now)) {
      return false;
    }

    Duration timeDiff = Duration.between(this.lastActiveAt, now);

    return timeDiff.toMinutes() <= LOGIN_TIMEOUT_MINUTES;
  }

//  public static UserStatusResponseDTO toDTO(UserStatus userStatus) {
//    UserStatusResponseDTO userStatusResponseDTO = new UserStatusResponseDTO(userStatus.getId(),
//        userStatus.getCreatedAt(),
//        userStatus.getUpdatedAt(),
//        userStatus.getUserId(),
//        userStatus.getLastActiveAt());
//
//    return userStatusResponseDTO;
//  }


  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserStatus that = (UserStatus) o;
    return Objects.equals(user, that.user);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(user);
  }
}
