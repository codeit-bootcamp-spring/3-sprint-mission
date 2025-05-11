package com.sprint.mission.discodeit.exception.notfound;

public class NotFoundBinaryContentException extends NotFoundException {
    public NotFoundBinaryContentException() {
        super("존재하지 않는 파일입니다.");
    }

    public NotFoundBinaryContentException(String message) {
        super(message);
    }
}
