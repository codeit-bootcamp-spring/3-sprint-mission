package com.sprint.mission.discodeit.exception.notfound;

public class NotFoundUserStatusException extends NotFoundException {
    public NotFoundUserStatusException() {
        super("해당 UserStatus가 존재하지 않습니다.");
    }

    public NotFoundUserStatusException(String message) {
        super(message);
    }
}
