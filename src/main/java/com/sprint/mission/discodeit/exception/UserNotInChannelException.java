package com.sprint.mission.discodeit.exception;

public class UserNotInChannelException extends RuntimeException {
    public UserNotInChannelException() {
        super("채널에 사용자가 속해 있지 않습니다.");
    }

    public UserNotInChannelException(String message) {
        super(message);
    }
}
