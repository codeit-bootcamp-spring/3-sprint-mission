package com.sprint.mission.discodeit.exception.authException;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

/**
 * PackageName  : com.sprint.mission.discodeit.exception.authException
 * FileName     : UnauthorizedTokenException
 * Author       : dounguk
 * Date         : 2025. 8. 17.
 */
public class UnauthorizedTokenException extends AuthException {
    public UnauthorizedTokenException(Map<String, Object> details) {
        super(ErrorCode.UNAUTHORIZED_TOKEN, details);
    }
    public UnauthorizedTokenException() {
        super(ErrorCode.UNAUTHORIZED_TOKEN);
    }
}
