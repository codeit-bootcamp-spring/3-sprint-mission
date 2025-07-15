package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class UserEmailAlreadyExistException extends UserException {

    public UserEmailAlreadyExistException() {
        super(ErrorCode.USER_EMAIL_ALREADY_EXISTS);
    }

    public static UserEmailAlreadyExistException withEmail(String email) {
        UserEmailAlreadyExistException exception = new UserEmailAlreadyExistException();
        exception.addDetail("email", email);
        return exception;
    }
}