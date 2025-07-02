package com.sprint.mission.discodeit.exception.readstatus;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

/**
 * 읽음정보가 존재하지 않을 때 발생하는 예외입니다.
 */
public class ReadStatusNotFoundException extends ReadStatusException {
    public ReadStatusNotFoundException(String message) {
        super(message, ErrorCode.READ_STATUS_NOT_FOUND);
    }
    public ReadStatusNotFoundException(String message, Map<String, Object> details) {
        super(message, ErrorCode.READ_STATUS_NOT_FOUND, details);
    }
    public ReadStatusNotFoundException(String message, Object readStatusId) {
        super(message, ErrorCode.READ_STATUS_NOT_FOUND, Map.of("readStatusId", readStatusId));
    }
} 