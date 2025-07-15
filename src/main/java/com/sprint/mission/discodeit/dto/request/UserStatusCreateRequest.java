package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "사용자 상태 생성 요청 DTO")
public record UserStatusCreateRequest(
        @Schema(description = "사용자 ID", type = "string", format = "uuid",
                example = "123e4567-e89b-12d3-a456-426614174000")
        @NotBlank(message = "사용자 ID는 필수 입니다.")
        UUID userId,

        @Schema(description = "마지막 활동 시간", type = "string", format = "date-time",
                example = "2024-03-20T09:12:28Z")
        @NotBlank(message = "시간 정보는 필수 입니다.")
        Instant lastActiveAt
) {
}
