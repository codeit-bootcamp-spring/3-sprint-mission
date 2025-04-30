package com.sprint.mission.discodeit.exception;

public class ReadStatusAlreadyExistsException extends RuntimeException {

    public ReadStatusAlreadyExistsException() {
        super("해당하는 유저,채널의 readStatus가 이미 존재합니다.");
    }
}
