package com.sprint.mission.discodeit.exception;

import java.time.Instant;
import java.util.Map;

public class NotificationException extends DiscodeitException {
    public NotificationException(String message) {
        super(message, Instant.now(), ErrorCode.INTERNAL_SERVER_ERROR, Map.of());
    }
    public NotificationException(String message, ErrorCode errorCode) {
        super(message, Instant.now(), errorCode, Map.of());
    }
    public NotificationException(String message, ErrorCode errorCode, Map<String, Object> details) {
        super(message, Instant.now(), errorCode, details);
    }
}
