
package com.sprint.mission.discodeit.exception;

import lombok.Getter;

@Getter
public abstract class DiscodeitException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String details;

    protected DiscodeitException(ErrorCode errorCode, String details) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.details = details;
    }
}
