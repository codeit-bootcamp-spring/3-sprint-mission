package com.sprint.mission.discodeit.exception;

import java.util.Map;

/**
 * PackageName  : com.sprint.mission.discodeit.exception
 * FileName     : UserAlreadyExistsException
 * Author       : dounguk
 * Date         : 2025. 6. 18.
 */
public class UserAlreadyExistsException extends UserException {
    public UserAlreadyExistsException(Map<String, Object> details) {
        super(ErrorCode.USER_ALREADY_EXISTS, details);
    }
    public UserAlreadyExistsException() {
        super(ErrorCode.USER_ALREADY_EXISTS);
    }
}
