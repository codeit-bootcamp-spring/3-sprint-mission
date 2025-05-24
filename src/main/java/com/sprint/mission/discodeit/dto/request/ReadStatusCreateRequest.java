package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
@Schema(description = "생성할 메세지 읽음 상태 정보")
public final class ReadStatusCreateRequest {

  @Schema(description = "읽음 상태 정보가 적용될 사용자 ID", type = "string", format = "uuid")
  private UUID userId;

  @Schema(description = "확인할 채널 ID", type = "string", format = "uuid")
  private UUID channelId;

  @Schema(description = "마지막으로 메세지를 읽은 시간", type = "string", format = "date-time")
  private Instant lastReadAt;
}
