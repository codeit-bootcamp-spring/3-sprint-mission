package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

public class UserException extends DiscodeitException {
    public UserException(ErrorCode code, Object details) {
        super(code, details);
    }
}
