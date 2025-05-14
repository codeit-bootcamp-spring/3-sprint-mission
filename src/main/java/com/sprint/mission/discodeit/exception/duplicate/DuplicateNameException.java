package com.sprint.mission.discodeit.exception.duplicate;

public class DuplicateNameException extends DuplicateException {
    public DuplicateNameException(String userName) {
        super(userName + "은 이미 존재합니다.");
    }
}
