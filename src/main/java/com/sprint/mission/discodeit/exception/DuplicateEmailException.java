package com.sprint.mission.discodeit.exception;

public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException() {
        super("이미 존재하는 email 입니다.");
    }

    public DuplicateEmailException(String message) {
        super(message);
    }
}
