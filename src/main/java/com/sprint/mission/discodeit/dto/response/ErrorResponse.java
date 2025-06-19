package com.sprint.mission.discodeit.dto.response;

import java.time.Instant;
import java.util.Map;

public record ErrorResponse(
    Instant timestamp,
    String code,
    String message,
    Map<String, Object> details,
    String exceptionType,
    int status
) {

    public static ErrorResponse of(
        String code,
        String message,
        Map<String, Object> details,
        String exceptionType,
        int status
    ) {
        return new ErrorResponse(Instant.now(), code, message, details, exceptionType, status);
    }
}
