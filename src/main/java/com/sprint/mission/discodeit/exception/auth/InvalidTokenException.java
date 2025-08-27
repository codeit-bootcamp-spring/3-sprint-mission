package com.sprint.mission.discodeit.exception.auth;

import com.sprint.mission.discodeit.dto.response.ErrorCode;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import java.time.Instant;
import java.util.Map;

public class InvalidTokenException extends DiscodeitException {

    public InvalidTokenException(String message) {
        super(
            Instant.now(),
            ErrorCode.INVALID_JWT_TOKEN,
            Map.of("message", message)
        );
    }
}
