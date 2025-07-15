package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.time.Instant;

@Schema(description = "사용자 상태 업데이트 요청 DTO")
public record UserStatusUpdateRequest(
        @Schema(description = "새로운 마지막 활동 시간", type = "string", format = "date-time",
                example = "2024-03-20T09:12:28Z")
        @NotBlank(message = "시간 정보는 필수 입니다.")
        Instant newLastActiveAt
) {
}
