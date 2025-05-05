package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.common.model.Auditable;
import java.io.Serial;
import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;
import lombok.ToString;

/**
 * 사용자의 상태 관리
 * <p>
 * <ul>
 * <li>AuditInfo (id, createdAt, updatedAt)</li>
 * <li>사용자 ID</li>
 * <li>마지막 접속 시간</li>
 * </ul>
 */
@Getter
@ToString(callSuper = true)
public class UserStatus extends Auditable implements Serializable {

  @Serial
  private static final long serialVersionUID = -7917996053260213133L;

  // 마지막 접속 시간이 현재 시간으로부터 5분 이내이면 현재 접속 중인 유저로 간주합니다.
  private static final Duration ACTIVE_DURATION = Duration.ofMinutes(5);

  // 참조 정보
  private final UUID userId;
  private Instant lastActiveAt;

  // 외부에서 직접 객체 생성 방지
  private UserStatus(UUID userId) {
    this.userId = userId;
    this.lastActiveAt = getCreatedAt(); // 초기값은 생성 시간으로 설정
  }

  // 정적 팩토리 메서드로 명시적인 생성
  public static UserStatus create(UUID userId) {
    UserStatus userStatus = new UserStatus(userId);
    userStatus.touch(); // 초기 updatedAt 설정
    return userStatus;
  }

  public void updateLastActiveAt() {
    this.lastActiveAt = Instant.now();
    touch();
  }

  /**
   * 마지막 접속 시간이 현재 시간으로부터 5분 이내인지 확인하여 현재 접속 중인 유저인지 판단
   *
   * @return 현재 접속 중인 유저이면 true, 아니면 false
   */
  public boolean isCurrentlyActive() {
    Instant now = Instant.now();
    Duration duration = Duration.between(lastActiveAt, now);
    return duration.compareTo(ACTIVE_DURATION) <= 0;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserStatus that = (UserStatus) o;
    return Objects.equals(getId(), that.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId());
  }
}