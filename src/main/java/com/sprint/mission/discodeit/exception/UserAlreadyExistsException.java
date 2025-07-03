package com.sprint.mission.discodeit.exception;

public class UserAlreadyExistsException extends UserException {

    public UserAlreadyExistsException() {
        super(ErrorCode.DUPLICATE_USER);
    }

    public static UserAlreadyExistsException withEmail(String email) {
        UserAlreadyExistsException exception = new UserAlreadyExistsException();
        exception.addDetail("email", email);
        return exception;
    }

    public static UserAlreadyExistsException withUsername(String username) {
        UserAlreadyExistsException exception = new UserAlreadyExistsException();
        exception.addDetail("username", username);
        return exception;
    }
}
