package com.sprint.mission.discodeit.exception.notification;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public abstract class NotificationException extends DiscodeitException {

    /**
     * 직접 예외를 던지기 보다는 계층 구조 명확성을 위한 클래스이므로 상속 전용으로 사용
     * 하기 위해 protected 타입 사용
     */
    protected NotificationException(ErrorCode errorCode) {
        super(errorCode);
    }

    protected NotificationException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }

    protected NotificationException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    protected NotificationException(ErrorCode errorCode, Map<String, Object> details, Throwable cause) {
        super(errorCode, details, cause);
    }

    protected NotificationException(ErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }

    protected NotificationException(ErrorCode errorCode, String customMessage, Map<String, Object> details) {
        super(errorCode, customMessage, details);
    }
}
