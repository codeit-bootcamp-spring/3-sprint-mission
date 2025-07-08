package com.sprint.mission.discodeit.exception.readstatus;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import java.time.Instant;
import java.util.Map;

/**
 * 리드상태(ReadStatus) 도메인에서 발생하는 예외의 상위 클래스입니다.
 * <p>구체적인 리드상태 예외는 이 클래스를 상속받아 구현합니다.</p>
 */
public abstract class ReadStatusException extends DiscodeitException {
    public ReadStatusException(String message) {
        super(message, Instant.now(), ErrorCode.INVALID_ARGUMENT, Map.of());
    }
    public ReadStatusException(String message, ErrorCode errorCode) {
        super(message, Instant.now(), errorCode, Map.of());
    }
    public ReadStatusException(String message, ErrorCode errorCode, Map<String, Object> details) {
        super(message, Instant.now(), errorCode, details);
    }
} 