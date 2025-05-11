package com.sprint.mission.discodeit.exception.notfound;

public class NotFoundMessageException extends NotFoundException {
    public NotFoundMessageException() {
        super("존재하지 않는 메시지입니다.");
    }

    public NotFoundMessageException(String message) {
        super(message);
    }
}
