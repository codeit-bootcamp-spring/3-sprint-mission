package com.sprint.mission.discodeit.exception;

import org.springframework.http.HttpStatus;

import java.util.Map;

/**
 * PackageName  : com.sprint.mission.discodeit.exception
 * FileName     : MessageException
 * Author       : dounguk
 * Date         : 2025. 6. 20.
 */
public class MessageException extends DiscodeitException {
    public MessageException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }

    public MessageException(ErrorCode errorCode) {
        super(errorCode);
    }
}
