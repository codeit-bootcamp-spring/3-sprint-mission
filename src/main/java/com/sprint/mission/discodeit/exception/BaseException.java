package com.sprint.mission.discodeit.exception;

import org.springframework.http.HttpStatus;

public abstract class BaseException extends RuntimeException {

    public abstract String getCode();
    public abstract String getMessage();
    public abstract HttpStatus getHttpStatus();
}
