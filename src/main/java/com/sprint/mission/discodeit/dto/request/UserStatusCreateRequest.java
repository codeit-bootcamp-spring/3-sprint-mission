package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

public record UserStatusCreateRequest(
    @Schema(description = "유저 ID", example = "e1a2b3c4-d5f6-7890-abcd-ef1234567890")
    UUID userId,

    @Schema(description = "마지막 온라인 시간", example = "2025-05-16T14:00:00Z")
    Instant lastActiveAt
) {

}
