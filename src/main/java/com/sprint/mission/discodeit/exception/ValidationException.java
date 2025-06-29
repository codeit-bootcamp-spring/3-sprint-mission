package com.sprint.mission.discodeit.exception;

import java.util.Map;

public class ValidationException extends DiscodeitException {

    public ValidationException(String message, Map<String, Object> details) {
        super(ErrorCode.VALIDATION_FAILED, message, details);
    }

    public ValidationException(String message) {
        super(ErrorCode.VALIDATION_FAILED, message, Map.of());
    }

    public ValidationException(Map<String, Object> details) {
        super(ErrorCode.VALIDATION_FAILED, details);
    }
}
