package com.sprint.mission.discodeit.exception;

public class DuplicateNameException extends RuntimeException {
    public DuplicateNameException(String userName) {
        super(userName + "은 이미 존재합니다.");
    }
}
