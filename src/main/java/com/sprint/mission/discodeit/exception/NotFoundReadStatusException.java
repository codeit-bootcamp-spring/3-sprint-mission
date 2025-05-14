package com.sprint.mission.discodeit.exception;

public class NotFoundReadStatusException extends RuntimeException {
    public NotFoundReadStatusException() {
        super("존재하지 않는 ReadStatus 입니다.");
    }

    public NotFoundReadStatusException(String message) {
        super(message);
    }
}
