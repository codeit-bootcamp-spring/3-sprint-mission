package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor
@Schema(description = "변경하려는 사용자 온라인 정보")
public final class UserStatusUpdateRequest {

  @Schema(description = "변경할 마지막 접속 시간", example = "2022-01-01T00:00:00Z", type = "string", format = "data-time")
  private Instant newLastActiveAt;
}
