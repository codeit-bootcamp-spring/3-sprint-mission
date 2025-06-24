package com.sprint.mission.discodeit.exception.userStatus;

import com.sprint.mission.discodeit.dto.ErrorCode;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class UserStatusNotFoundException extends UserStatusException {

    public UserStatusNotFoundException(UUID userStatusId) {
        super(
                Instant.now(),
                ErrorCode.USER_STATUS_NOT_FOUND,
                Map.of("userStatusId", userStatusId)
        );
    }
}
