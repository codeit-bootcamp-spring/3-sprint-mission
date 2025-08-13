package com.sprint.mission.discodeit.exception.readstatus;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

/**
 * 읽음 상태가 중복될 때 발생하는 예외입니다.
 */
public class DuplicatedReadStatusException extends ReadStatusException {
    public DuplicatedReadStatusException(String message) {
        super(message, ErrorCode.DUPLICATED_READ_STATUS);
    }
    public DuplicatedReadStatusException(String message, Map<String, Object> details) {
        super(message, ErrorCode.DUPLICATED_READ_STATUS, details);
    }
    public DuplicatedReadStatusException(String message, Object userId, Object channelId) {
        super(message, ErrorCode.DUPLICATED_READ_STATUS, Map.of("userId", userId, "channelId", channelId));
    }
} 