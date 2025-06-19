package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.time.Instant;
import java.util.Map;

public class UserAlreadyExistException extends UserException {

    public UserAlreadyExistException(ErrorCode errorCode,
        Map<String, Object> details) {
        super(errorCode, details);
    }
}
