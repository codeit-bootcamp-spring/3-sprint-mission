package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class UserNameAlreadyExistException extends UserException {

    public UserNameAlreadyExistException() {
        super(ErrorCode.USER_NAME_ALREADY_EXISTS);
    }

    public static UserNameAlreadyExistException withUsername(String username) {
        UserNameAlreadyExistException exception = new UserNameAlreadyExistException();
        exception.addDetail("username", username);
        return exception;
    }
}