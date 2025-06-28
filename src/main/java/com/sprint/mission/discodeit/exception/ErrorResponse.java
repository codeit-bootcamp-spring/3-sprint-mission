package com.sprint.mission.discodeit.exception;

import java.time.Instant;
import java.util.Map;
import org.springframework.http.HttpStatus;

public record ErrorResponse(
        Instant timestamp,
        String code,
        String message,
        Map<String, Object> details,
        String exceptionType,
        int status
) {

    public static ErrorResponse of(HttpStatus status, DiscodeitException error
    ) {
        return new ErrorResponse(
                error.getTimestamp(),
                error.getErrorCode().name(),
                error.getErrorCode().getMessage(),
                error.getDetails(),
                error.getClass().getSimpleName(),
                status.value()
        );
    }
}