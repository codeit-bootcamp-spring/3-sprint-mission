package com.sprint.mission.discodeit.exception;

import java.util.Map;

/**
 * PackageName  : com.sprint.mission.discodeit.exception
 * FileName     : UserNotFoundException
 * Author       : dounguk
 * Date         : 2025. 6. 18.
 */
public class UserNotFoundException extends UserException {
    public UserNotFoundException(Map<String, Object> details) {
        super(ErrorCode.USER_NOT_FOUND, details);
    }
    public UserNotFoundException() {
        super(ErrorCode.USER_NOT_FOUND);
    }
}
