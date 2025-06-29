package com.sprint.mission.discodeit.exception;

public class DiscodeitException extends RuntimeException {
    private final ErrorCode errorCode;
    private final Object details;

    public DiscodeitException(ErrorCode errorCode) {
        this(errorCode, null, null);
    }

    public DiscodeitException(ErrorCode errorCode, Object details) {
        this(errorCode, null, details);
    }

    public DiscodeitException(ErrorCode errorCode, Throwable cause, Object details) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
        this.details = details;
    }

    public ErrorCode getErrorCode() { return errorCode; }
    public Object getDetails() { return details; }
}
