package com.sprint.mission.discodeit.exception.jwt;

import com.sprint.mission.discodeit.entity.ErrorCode;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import java.util.Map;

public class InvalidTokenException extends DiscodeitException {

    public InvalidTokenException(String refreshToken) {
        super(
            ErrorCode.INVALID_TOKEN.getMessage() + "Refresh Token: " + refreshToken,
            ErrorCode.INVALID_TOKEN,
            Map.of("refreshToken", refreshToken)
        );
    }
}
