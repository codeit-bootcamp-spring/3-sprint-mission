package com.sprint.mission.discodeit.exception.userStatus;

import com.sprint.mission.discodeit.dto.ErrorCode;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class DuplicateUserStatusException extends UserStatusException {
    public DuplicateUserStatusException(UUID userId) {
        super(
                Instant.now(),
                ErrorCode.DUPLICATE_USER_STATUS,
                Map.of("userId", userId)
        );
    }
}
