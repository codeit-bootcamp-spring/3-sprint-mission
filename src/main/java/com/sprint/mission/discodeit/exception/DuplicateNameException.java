package com.sprint.mission.discodeit.exception;

public class DuplicateNameException extends RuntimeException {
    public DuplicateNameException() {
        super("이미 존재하는 username 입니다.");
    }

    public DuplicateNameException(String message) {
        super(message);
    }
}
