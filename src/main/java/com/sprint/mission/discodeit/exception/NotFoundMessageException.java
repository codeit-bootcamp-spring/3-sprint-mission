package com.sprint.mission.discodeit.exception;

public class NotFoundMessageException extends RuntimeException {
    public NotFoundMessageException() {
        super("존재하지 않는 메시지입니다.");
    }

    public NotFoundMessageException(String message) {
        super(message);
    }
}
