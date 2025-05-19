package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
@Schema(description = "생성할 사용자 온라인 정보")
public final class UserStatusCreateRequest {

  @Schema(description = "사용자 상태 정보를 생성할 사용자 ID", type = "string", format = "uuid")
  @NotBlank(message = "생성하고자하는 사용자 대상의 ID는 필수입니다")
  private UUID userId;

  @Schema(description = "생성할 사용자 대상의 마지막 온라인 시간 정보", type = "string", format = "date-time")
  @NotBlank
  private Instant lastOnlineAt;
}
