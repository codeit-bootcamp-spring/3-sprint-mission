package com.sprint.mission.discodeit.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

// 사용자별 마지막 접속 시간( last+5 : 온라인 ) / 판별 메서드 정의
@Getter
@Schema(description = "사용자 활동 상태 정보 도메인 모델")
public class UserStatus implements Serializable {

  private static final long serialVersionUID = 1L;
  //
  @Schema(
      description = "활동 상태 고유 번호",
      example = "b1fca423-964d-294d-492h482940vf",
      type = "string",
      format = "uuid"
  )
  private final UUID id;

  @Schema(
      description = "활동 상태 정보 생성 시각",
      example = "2025-05-15T17:18:08Z",
      type = "string",
      format = "date-time"
  )
  private final Instant createdAt;

  @Schema(
      description = "활동 상태 정보 수정 시각",
      example = "2025-05-15T17:19::09Z",
      type = "string",
      format = "date-time"
  )
  private Instant updatedAt;
  //
  @Schema(
      description = "활동 상태를 확인할 사용자 대상의 고유 번호",
      example = "2948gd12-0362-f5g0-48612695f5d9",
      type = "string",
      format = "uuid"
  )
  private UUID userId;

  @Schema(
      description = "마지막 접속 시각",
      example = "2025-05-15T17:21:45Z",
      type = "string",
      format = "date-time"
  )
  private Instant lastActiveAt;

  // 생성자
  public UserStatus(UUID userId, Instant lastActiveAt) {
    this.id = UUID.randomUUID();
    this.createdAt = Instant.now();
    //
    this.userId = userId;
    this.lastActiveAt = lastActiveAt;
  }

  // Update 메서드
  public void update(Instant lastOnlineAt) {
    boolean updated = false;
    if (lastOnlineAt != null && lastOnlineAt.equals(this.lastActiveAt)) {
      this.lastActiveAt = lastOnlineAt;
      updated = true;
    }

    if (updated) {
      this.updatedAt = Instant.now();
    }
  }

  // 온라인 상태 판별 메서드 정의
  public boolean isOnline() {
    Instant instantFiveMinutesAgo = Instant.now().minus(Duration.ofMinutes(5));

    return lastActiveAt.isAfter(instantFiveMinutesAgo);
  }
}
