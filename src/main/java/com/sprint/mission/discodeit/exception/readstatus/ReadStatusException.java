package com.sprint.mission.discodeit.exception.readstatus;

import com.sprint.mission.discodeit.entity.ErrorCode;
import com.sprint.mission.discodeit.exception.DiscodeitException;

import java.util.Map;

public class ReadStatusException extends DiscodeitException {

    public ReadStatusException(String message, ErrorCode errorCode, Map<String, Object> details) {
        super(message, errorCode, details);
    }
}
