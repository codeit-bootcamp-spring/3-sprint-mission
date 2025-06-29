package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.time.Instant;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResponse {

    private Instant timestamp;
    private String code;
    private String message;
    private Map<String, Object> details;
    private String exceptionType;
    private int status;

    public static ErrorResponse of(ErrorCode error) {
        return ErrorResponse.builder()
            .timestamp(Instant.now())
            .code(error.getCode())
            .message(error.getMessage())
            .status(error.getStatus())
            .exceptionType(error.getClass().getSimpleName())
            .build();
    }

    public static ErrorResponse of(ErrorCode errorCode, Map<String, Object> details,
        String exceptionType) {
        return ErrorResponse.builder()
            .timestamp(Instant.now())
            .code(errorCode.getCode())
            .message(errorCode.getMessage())
            .status(errorCode.getStatus())
            .details(details)
            .exceptionType(exceptionType)
            .build();
    }
}