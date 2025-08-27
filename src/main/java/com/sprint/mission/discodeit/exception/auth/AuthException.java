package com.sprint.mission.discodeit.exception.auth;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class AuthException extends DiscodeitException {

    protected AuthException(ErrorCode errorCode) {
        super(errorCode);
    }

    protected AuthException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }

    protected AuthException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    protected AuthException(ErrorCode errorCode, Map<String, Object> details, Throwable cause) {
        super(errorCode, details, cause);
    }

    protected AuthException(ErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }

    protected AuthException(ErrorCode errorCode, String customMessage, Map<String, Object> details) {
        super(errorCode, customMessage, details);
    }
}
