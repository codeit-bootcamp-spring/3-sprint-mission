package com.sprint.mission.discodeit.exception;

public class NotFoundUserStatusException extends RuntimeException {
    public NotFoundUserStatusException() {
        super("해당 UserStatus가 존재하지 않습니다.");
    }

    public NotFoundUserStatusException(String message) {
        super(message);
    }
}
