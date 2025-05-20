package com.sprint.mission.discodeit.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Schema(
        name = "UserStatus",
        description = "사용자의 온라인 상태 정보를 담고 있는 엔티티 (마지막 활동 시점으로부터 5분 이내면 온라인으로 간주)"
)
@Getter
public class UserStatus implements Serializable {

  private static final long serialVersionUID = 1L;

  @Schema(
          description = "사용자 상태의 고유 식별자",
          type = "string",
          format = "uuid",
          example = "123e4567-e89b-12d3-a456-426614174000"
  )
  private UUID id;

  @Schema(
          description = "사용자 상태 엔티티 생성 시간",
          type = "string",
          format = "date-time",
          example = "2024-03-20T09:12:28Z"
  )
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private Instant createdAt;

  @Schema(
          description = "사용자 상태 최종 수정 시간",
          type = "string",
          format = "date-time",
          example = "2024-03-20T09:12:28Z"
  )
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private Instant updatedAt;

  @Schema(
          description = "상태 정보의 소유자 ID",
          type = "string",
          format = "uuid",
          example = "123e4567-e89b-12d3-a456-426614174000"
  )
  private UUID userId;

  @Schema(
          description = "사용자의 마지막 활동 시간",
          type = "string",
          format = "date-time",
          example = "2024-03-20T09:12:28Z"
  )
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private Instant lastActiveAt;


  public UserStatus(UUID userId, Instant lastActiveAt) {
    this.id = UUID.randomUUID();
    this.createdAt = Instant.now();
    //
    this.userId = userId;
    this.lastActiveAt = lastActiveAt;
  }

  public void update(Instant lastActiveAt) {
    boolean anyValueUpdated = false;
    if (lastActiveAt != null && !lastActiveAt.equals(this.lastActiveAt)) {
      this.lastActiveAt = lastActiveAt;
      anyValueUpdated = true;
    }

    if (anyValueUpdated) {
      this.updatedAt = Instant.now();
    }
  }

  public Boolean isOnline() {
    Instant instantFiveMinutesAgo = Instant.now().minus(Duration.ofMinutes(5));

    return lastActiveAt.isAfter(instantFiveMinutesAgo);
  }
}
