package com.sprint.mission.discodeit.exception;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record ErrorResponse(
        int status,
        String message,
        String path,
        LocalDateTime timestamp
) {
}