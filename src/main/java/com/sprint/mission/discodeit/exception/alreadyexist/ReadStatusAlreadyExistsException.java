package com.sprint.mission.discodeit.exception.alreadyexist;

public class ReadStatusAlreadyExistsException extends RuntimeException {
    public ReadStatusAlreadyExistsException() {
        super("해당 User, Channel과 관련된 상태가 이미 존재합니다.");
    }

    public ReadStatusAlreadyExistsException(String message) {
        super(message);
    }
}
