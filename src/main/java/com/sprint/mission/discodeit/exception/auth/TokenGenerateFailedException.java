package com.sprint.mission.discodeit.exception.auth;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class TokenGenerateFailedException extends AuthException {

    public TokenGenerateFailedException(String message) {
        super(ErrorCode.FAILED_TOKEN_GENERATED, Map.of("message", message));
    }
}
