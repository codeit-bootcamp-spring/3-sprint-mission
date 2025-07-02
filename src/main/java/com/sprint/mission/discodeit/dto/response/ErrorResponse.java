package com.sprint.mission.discodeit.dto.response;

import java.time.Instant;
import java.util.Map;
import lombok.Builder;
import org.springframework.http.HttpStatus;

@Builder
public record ErrorResponse(
    Instant timestamp,
    String code,
    String message,
    Map<String, Object> details,
    String exceptionType,
    int status
) {

    public static ErrorResponse of(Instant timestamp, String code, String message,
        Map<String, Object> details, String exceptionType, HttpStatus status) {
        return ErrorResponse.builder()
            .timestamp(timestamp)
            .code(code)
            .message(message)
            .details(details)
            .exceptionType(exceptionType)
            .status(status.value())
            .build();
    }
}