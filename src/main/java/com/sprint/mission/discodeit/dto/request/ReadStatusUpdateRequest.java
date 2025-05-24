package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor
@Schema(description = "변경될 메세지 읽음 상태 정보")
public final class ReadStatusUpdateRequest {

  @Schema(description = "변경될 마지막 메세지 읽음 정보 시간", type = "string", format = "date-time")
  private Instant newLastReadAt;
}
