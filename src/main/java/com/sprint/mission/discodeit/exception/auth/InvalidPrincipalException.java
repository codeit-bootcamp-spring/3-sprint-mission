package com.sprint.mission.discodeit.exception.auth;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class InvalidPrincipalException extends AuthException {

    public InvalidPrincipalException(String message) {
        super(ErrorCode.INVALID_PRINCIPAL, Map.of("message", message));
    }
}
