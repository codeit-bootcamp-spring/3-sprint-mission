package com.sprint.mission.discodeit.exception.duplicate;

public class DuplicateEmailException extends DuplicateException {
    public DuplicateEmailException(String email) {
        super(email + "은 이미 존재하는 email 입니다.");
    }
}
