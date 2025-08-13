package com.sprint.mission.discodeit.exception.userstatus;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import java.time.Instant;
import java.util.Map;

/**
 * UserStatus 도메인에서 발생하는 예외의 상위 클래스입니다.
 * <p>구체적인 UserStatus 예외는 이 클래스를 상속받아 구현합니다.</p>
 */
public abstract class UserStatusException extends DiscodeitException {
    public UserStatusException(String message) {
        super(message, Instant.now(), ErrorCode.USER_STATUS_NOT_FOUND, Map.of());
    }
    public UserStatusException(String message, ErrorCode errorCode) {
        super(message, Instant.now(), errorCode, Map.of());
    }
    public UserStatusException(String message, ErrorCode errorCode, Map<String, Object> details) {
        super(message, Instant.now(), errorCode, details);
    }
} 