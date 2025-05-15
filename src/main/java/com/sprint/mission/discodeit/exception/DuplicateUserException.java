package com.sprint.mission.discodeit.exception;

public class DuplicateUserException extends RuntimeException {
    public DuplicateUserException() {
        super("해당하는 이메일/이름을 가진 유저가 이미 존재합니다");
    }
}
