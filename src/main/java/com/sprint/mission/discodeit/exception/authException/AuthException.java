package com.sprint.mission.discodeit.exception.authException;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

/**
 * PackageName  : com.sprint.mission.discodeit.exception.authException
 * FileName     : AuthException
 * Author       : dounguk
 * Date         : 2025. 8. 17.
 */
public class AuthException extends DiscodeitException {
    public AuthException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }

    public AuthException(ErrorCode errorCode) {
        super(errorCode);
    }
}
