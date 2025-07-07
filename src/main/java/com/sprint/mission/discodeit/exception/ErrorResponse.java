package com.sprint.mission.discodeit.exception;


import java.time.Instant;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class ErrorResponse {

    private final Instant timestamp;
    private final String code;
    private final String message;
    private final String exceptionType;
    private final int status;
    private final Map<String, Object> details;

    public ErrorResponse(HttpStatus status, ErrorCode errorCode) {
        this.timestamp = Instant.now();
        this.code = status.getReasonPhrase();
        this.message = errorCode.getMessage();
        this.exceptionType = errorCode.name();
        this.status = status.value();
        this.details = null;
    }


    public ErrorResponse(HttpStatus status, ErrorCode errorCode, Map<String, Object> details) {
        this.timestamp = Instant.now();
        this.code = status.getReasonPhrase();
        this.message = errorCode.getMessage();
        this.exceptionType = errorCode.name();
        this.status = status.value();
        this.details = details;
    }
}
