package com.sprint.mission.discodeit.exception.auth;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class InvalidTokenException extends AuthException {

    public InvalidTokenException(String message) {
        super(ErrorCode.INVALID_JWT_TOKEN, Map.of("message", message));
    }
}
