package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public abstract class UserException extends DiscodeitException {

    /**
     * 직접 예외를 던지기 보다는 계층 구조 명확성을 위한 클래스이므로 상속 전용으로 사용
     * 하기 위해 protected 타입 사용
     */
    protected UserException(ErrorCode errorCode) {
        super(errorCode);
    }

    protected UserException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }

    protected UserException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    protected UserException(ErrorCode errorCode, Map<String, Object> details, Throwable cause) {
        super(errorCode, details, cause);
    }

    protected UserException(ErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }

    protected UserException(ErrorCode errorCode, String customMessage, Map<String, Object> details) {
        super(errorCode, customMessage, details);
    }
}
