package com.sprint.mission.discodeit.dto.response;


import com.sprint.mission.discodeit.exception.DiscodeitException;
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

    public static ErrorResponse from(DiscodeitException ex, int status) {
        return new ErrorResponse(
            ex.getTimestamp(),
            ex.getErrorCode().name(),
            ex.getErrorCode().getMessage(),
            ex.getDetails(),
            ex.getClass().getSimpleName(),
            status
        );
    }
}