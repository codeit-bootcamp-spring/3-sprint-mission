package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.dto.response.ErrorCode;
import java.time.Instant;
import java.util.Map;

public class AuthenticationFailedException extends UserException {

    public AuthenticationFailedException(String username) {
        super(Instant.now(), ErrorCode.AUTHENTICATION_FAILED, Map.of("username", username));
    }
}