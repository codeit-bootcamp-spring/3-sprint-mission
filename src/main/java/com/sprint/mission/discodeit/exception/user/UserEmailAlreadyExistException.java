package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class UserEmailAlreadyExistException extends UserException {

    public UserEmailAlreadyExistException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }
}