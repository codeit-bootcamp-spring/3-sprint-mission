package com.sprint.mission.discodeit.exception;

public class UserException extends DiscodeitException {

    public UserException(ErrorCode errorCode) {
        super(errorCode);
    }
}
