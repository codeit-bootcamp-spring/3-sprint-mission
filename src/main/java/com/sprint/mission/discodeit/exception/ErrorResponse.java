package com.sprint.mission.discodeit.exception;

import java.time.Instant;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
public class ErrorResponse {

    private Instant timestamp;
    private String code;
    private String message;
    private Map<String, Object> details;
    private String exceptionType;
    private int status;

    // 완전체 응답
    public ErrorResponse(Instant timestamp, String code, String message,
            Map<String, Object> details,
            String exceptionType, int status) {
        this.timestamp = timestamp;
        this.code = code;
        this.message = message;
        this.details = details;
        this.exceptionType = exceptionType;
        this.status = status;
    }

    // 간단한 응답
    public ErrorResponse(Instant timestamp, String code, String message,
            String exceptionType, int status) {
        this.timestamp = timestamp;
        this.code = code;
        this.message = message;
        this.exceptionType = exceptionType;
        this.status = status;
    }


    public static ErrorResponse of(DiscodeitException ex) {
        return ErrorResponse.builder()
                .timestamp(ex.getTimestamp())
                .code(ex.getErrorCode().name())
                .message(ex.getErrorCode().getMessage())
                .details(ex.getDetails())
                .exceptionType(ex.getClass().getSimpleName())
                .status(ex.getErrorCode().getStatus())
                .build();
    }
}
