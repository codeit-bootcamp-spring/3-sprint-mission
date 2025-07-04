package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.dto.ErrorCode;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class UserNotFoundException extends UserException {

    public UserNotFoundException(UUID userId) {
        super(
                Instant.now(),
                ErrorCode.USER_NOT_FOUND,
                Map.of("userId", userId)
        );
    }

    public UserNotFoundException(String username) {
        super(
                Instant.now(),
                ErrorCode.USER_NOT_FOUND,
                Map.of("username", username)
        );
    }
}
