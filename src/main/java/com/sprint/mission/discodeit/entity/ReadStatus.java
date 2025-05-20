package com.sprint.mission.discodeit.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Schema(
        name = "ReadStatus",
        description = "사용자의 채널별 메시지 읽음 상태 정보를 담고 있는 엔티티"
)
@Getter
public class ReadStatus implements Serializable {

  private static final long serialVersionUID = 1L;

  @Schema(
          description = "읽음 상태의 고유 식별자",
          type = "string",
          format = "uuid",
          example = "123e4567-e89b-12d3-a456-426614174000"
  )
  private UUID id;

  @Schema(
          description = "읽음 상태 생성 시간",
          type = "string",
          format = "date-time",
          example = "2024-03-20T09:12:28Z"
  )
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private Instant createdAt;

  @Schema(
          description = "읽음 상태 최종 수정 시간",
          type = "string",
          format = "date-time",
          example = "2024-03-20T09:12:28Z"
  )
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private Instant updatedAt;

  @Schema(
          description = "읽음 상태의 소유자 ID",
          type = "string",
          format = "uuid",
          example = "123e4567-e89b-12d3-a456-426614174000"
  )
  private UUID userId;

  @Schema(
          description = "읽음 상태가 적용되는 채널 ID",
          type = "string",
          format = "uuid",
          example = "123e4567-e89b-12d3-a456-426614174000"
  )
  private UUID channelId;

  @Schema(
          description = "사용자가 채널의 메시지를 마지막으로 읽은 시간",
          type = "string",
          format = "date-time",
          example = "2024-03-20T09:12:28Z"
  )
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private Instant lastReadAt;


  public ReadStatus(UUID userId, UUID channelId, Instant lastReadAt) {
    this.id = UUID.randomUUID();
    this.createdAt = Instant.now();
    //
    this.userId = userId;
    this.channelId = channelId;
    this.lastReadAt = lastReadAt;
  }

  public void update(Instant newLastReadAt) {
    boolean anyValueUpdated = false;
    if (newLastReadAt != null && !newLastReadAt.equals(this.lastReadAt)) {
      this.lastReadAt = newLastReadAt;
      anyValueUpdated = true;
    }

    if (anyValueUpdated) {
      this.updatedAt = Instant.now();
    }
  }
}
