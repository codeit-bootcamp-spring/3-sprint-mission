package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.entity.ErrorCode;
import lombok.Getter;

import java.time.Instant;
import java.util.Map;

@Getter
public class DiscodeitException extends RuntimeException {

    private final Instant timestamp;
    private final ErrorCode errorCode;
    private final Map<String, Object> details;

    public DiscodeitException(String message, ErrorCode errorCode, Map<String, Object> details) {
        super(message);
        this.timestamp = Instant.now();
        this.errorCode = errorCode;
        this.details = details;
    }
}
