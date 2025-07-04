package com.sprint.mission.discodeit.exception.message;

import com.sprint.mission.discodeit.dto.ErrorCode;
import com.sprint.mission.discodeit.exception.DiscodeitException;

import java.time.Instant;
import java.util.Map;

public class MessageException extends DiscodeitException {
    public MessageException(Instant timestamp, ErrorCode errorCode, Map<String, Object> details) {
        super(timestamp, errorCode, details);
    }
}
