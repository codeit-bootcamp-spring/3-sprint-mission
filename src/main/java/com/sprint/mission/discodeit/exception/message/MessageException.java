package com.sprint.mission.discodeit.exception.message;

import com.sprint.mission.discodeit.entity.enums.ErrorCode;
import com.sprint.mission.discodeit.exception.DiscodeitException;

import java.util.Map;

public class MessageException extends DiscodeitException {

    public MessageException(String message, ErrorCode errorCode, Map<String, Object> details) {
        super(message, errorCode, details);
    }
}
