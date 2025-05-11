package com.sprint.mission.discodeit.exception;

public class UserAlreadyInChannelException extends RuntimeException {
    public UserAlreadyInChannelException(String message) {
        super(message);
    }
}
