package com.sprint.mission.discodeit.exception.auth;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class InvalidTokenException extends AuthException {

    public InvalidTokenException(String token) {
        super(ErrorCode.INVALID_TOKEN, Map.of("토큰", token));
    }
}
