package com.sprint.mission.discodeit.exception;

public class LoginFailedException extends RuntimeException {

    public LoginFailedException() {
        super("비밀번호가 일치하지 않습니다.");
    }

    public LoginFailedException(String message) {
        super(message);
    }
}
