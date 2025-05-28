package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "사용자 상태 생성 요청 DTO")
public record UserStatusCreateRequest(
        @Schema(description = "사용자 ID", type = "string", format = "uuid",
                example = "123e4567-e89b-12d3-a456-426614174000")
        UUID userId,

        @Schema(description = "마지막 활동 시간", type = "string", format = "date-time",
                example = "2024-03-20T09:12:28Z")
        Instant lastActiveAt
) {}
