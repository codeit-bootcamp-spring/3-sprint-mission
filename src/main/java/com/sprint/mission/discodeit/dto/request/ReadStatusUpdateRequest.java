package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
@Schema(description = "변경될 메세지 읽음 상태 정보")
public final class ReadStatusUpdateRequest {

  @Schema(description = "읽음 상태 정보가 수정될 사용자의 ID", type = "string", format = "uuid")
  @NotBlank(message = "정보가 수정될 사용자 대상의 ID는 필수입니다")
  private UUID id;

  @Schema(description = "변경될 마지막 메세지 읽음 정보 시간", type = "string", format = "date-time")
  @NotBlank
  private Instant newLastReadAt;
}
