package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.entity.ErrorCode;
import com.sprint.mission.discodeit.exception.DiscodeitException;

import java.util.Map;

public class UserException extends DiscodeitException {

    public UserException(String message, ErrorCode errorCode, Map<String, Object> details) {
        super(message, errorCode, details);
    }
}
