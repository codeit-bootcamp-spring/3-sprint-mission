package com.sprint.mission.discodeit.exception;

public class NotFoundBinaryContentException extends RuntimeException {
    public NotFoundBinaryContentException() {
        super("존재하지 않는 파일입니다.");
    }

    public NotFoundBinaryContentException(String message) {
        super(message);
    }
}
