package com.sprint.mission.discodeit.exception;

public class UserStatusAlreadyExistsException extends RuntimeException {
    public UserStatusAlreadyExistsException() {
        super("해당 User와 관련된 UserStatus가 이미 존재합니다.");
    }

    public UserStatusAlreadyExistsException(String message) {
        super(message);
    }
}
