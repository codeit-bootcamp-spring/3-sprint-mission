package com.sprint.mission.discodeit.exception;

public class NotFoundChannelException extends RuntimeException{
    public NotFoundChannelException() {
        super("존재하지 않는 채널입니다.");
    }

    public NotFoundChannelException(String message) {
        super(message);
    }
}
