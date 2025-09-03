package com.sprint.mission.discodeit.exception;

public class ForbiddenException extends RuntimeException {
    public ForbiddenException() {
        super("접근이 거부되었습니다.");
    }
}
