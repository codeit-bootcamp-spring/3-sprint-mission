package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class InvalidUserPasswordException extends UserException {

    public InvalidUserPasswordException(String password) {
        super(ErrorCode.INVALID_USER_PASSWORD);
    }
}
