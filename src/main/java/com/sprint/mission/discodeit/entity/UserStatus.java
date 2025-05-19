package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusResponseDTO;
import lombok.Getter;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * 사용자 별 마지막으로 확인된 접속 시간을 표현하는 도메인 모델 사용자의 온라인 상태를 확인하기 위해 활용
 */
@Getter
public class UserStatus implements Serializable {

  private static final long serialVersionUID = 1L;
  private UUID id;
  private final Instant createdAt;
  private Instant updatedAt;
  private final UUID userId;
  private Instant lastLoginTime;
  private static final long LOGIN_TIMEOUT_MINUTES = 5L;

  public UserStatus(UUID userId, Instant lastLoginTime) {
    this.id = UUID.randomUUID();
    this.createdAt = Instant.now();
    this.userId = userId;
    this.lastLoginTime = lastLoginTime;
  }

  public void updateLastLoginTime(Instant lastLoginTime) {
    this.updatedAt = Instant.now();
    this.lastLoginTime = lastLoginTime;
  }

  /**
   * 마지막 접속 시간을 기준으로 현재 로그인한 유저로 판단할 수 있는 메소드
   *
   * @return 마지막 접속 시간이 현재 시간으로부터 5분 이내인지
   */
  public boolean isLogin() {
    if (lastLoginTime == null) {
      return false;
    }

    Instant now = Instant.now();

    // lastLoginTime이 현재 시간보다 미래일 수는 없기 때문에 false 반환
    if (lastLoginTime.isAfter(now)) {
      return false;
    }

    Duration timeDiff = Duration.between(this.lastLoginTime, now);

    return timeDiff.toMinutes() <= LOGIN_TIMEOUT_MINUTES;
  }

  public static UserStatusResponseDTO toDTO(UserStatus userStatus) {
    UserStatusResponseDTO userStatusResponseDTO = new UserStatusResponseDTO(userStatus.getId(),
        userStatus.getCreatedAt(),
        userStatus.getUpdatedAt(),
        userStatus.getUserId(),
        userStatus.getLastLoginTime());

    return userStatusResponseDTO;
  }

  @Override
  public boolean equals(Object o) {
      if (o == null || getClass() != o.getClass()) {
          return false;
      }
    UserStatus that = (UserStatus) o;
    return Objects.equals(userId, that.userId);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(userId);
  }
}
