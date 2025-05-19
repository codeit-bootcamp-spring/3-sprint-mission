package com.sprint.mission.discodeit.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;


// 사용자가 해당 채널에서 마지막으로 언제 읽었는지 확인
@Getter
@Schema(description = "사용자가 채널에서 메세지를 읽은 시간 정보 도메인 모델")
public class ReadStatus implements Serializable {

  private static final long serialVersionUID = 1L;
  //
  @Schema(
      description = "읽은 상태를 구분하는 고유 식별 번호",
      example = "7aa22fb1-03d6-4df3-b9cd-dff7ac0c6d2f",
      type = "string",
      format = "uuid"
  )
  private final UUID readId;

  @Schema(
      description = "읽은 상태가 생성된 시각",
      example = "2025-05-10T10:30:00Z",
      type = "string",
      format = "date-time"
  )
  private final Instant createdAt;

  @Schema(
      description = "읽은 상태가 수정된 시각",
      example = "2025-05-12T15:45:00Z",
      type = "string",
      format = "date-time"
  )
  private Instant updatedAt;
  //
  @Schema(
      description = "읽은 사용자 대상의 고유 식별 번호",
      example = "df1c5063-9120-45e2-b27f-1c4db5f03c3a",
      type = "string",
      format = "uuid"
  )
  private UUID userId;

  @Schema(
      description = "읽은 메세지가 소속된 채널의 고유 식별 번호",
      example = "a92a2cf7-7bc7-4266-a67e-6ec219f3a843",
      type = "string",
      format = "uuid"
  )
  private UUID channelId;
  // 마지막으로 읽은 시간이 적합한가, 마지막으로 읽은 메세지가 적합한가.. << ERD 구조상 읽은 시간으로
  @Schema(
      description = "해당 사용자가 채널 내 마지막으로 메세지를 읽은 시각",
      example = "2025-05-15T17:32:22",
      type = "string",
      format = "date-time"
  )
  private Instant lastReadAt;

  // 생성자
  public ReadStatus(UUID userId, UUID channelId, Instant lastReadAt) {
    this.readId = UUID.randomUUID();
    this.createdAt = Instant.now();
    //
    this.userId = userId;
    this.channelId = channelId;
    this.lastReadAt = lastReadAt;
  }

  // Update 메서드
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
