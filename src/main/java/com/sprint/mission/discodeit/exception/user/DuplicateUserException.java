package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.dto.ErrorCode;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class DuplicateUserException extends UserException {
    public DuplicateUserException(UUID userId) {
        super(
                Instant.now(),
                ErrorCode.DUPLICATE_USER,
                Map.of("userId", userId)
        );
    }

    public DuplicateUserException(String username, String email) {
        super(
                Instant.now(),
                ErrorCode.DUPLICATE_USER,
                Map.of("username", username, "email", email)
        );
    }
}
