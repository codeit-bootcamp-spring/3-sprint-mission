package com.sprint.mission.discodeit.exception;

public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String email) {
        super(email + "은 이미 존재하는 email 입니다.");
    }
}
