package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

public record ReadStatusUpdateRequest(
    @Schema(description = "업데이트할 마지막 읽은 시간", example = "2025-05-16T15:30:00Z")
    Instant newLastReadAt
) {

}
