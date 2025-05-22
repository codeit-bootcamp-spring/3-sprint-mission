package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

public record UserStatusUpdateRequest(
    @Schema(description = "업데이트할 마지막 온라인 시간", example = "2025-05-16T14:00:00Z")
    Instant newLastActiveAt
) {

}
