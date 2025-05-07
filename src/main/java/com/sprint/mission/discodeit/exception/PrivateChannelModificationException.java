package com.sprint.mission.discodeit.exception;

public class PrivateChannelModificationException extends RuntimeException {
    public PrivateChannelModificationException() {
        super("PRIVATE 채널은 변경이 불가능합니다.");
    }

    public PrivateChannelModificationException(String message) {
        super(message);
    }
}
