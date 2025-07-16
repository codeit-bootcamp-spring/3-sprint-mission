package com.sprint.mission.discodeit.exception.userstatus;

import com.sprint.mission.discodeit.entity.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class NotFoundUserStatusException extends UserStatusException {

    public NotFoundUserStatusException() {
        super(
                ErrorCode.USER_STATUS_NOT_FOUND.getMessage(),
                ErrorCode.USER_STATUS_NOT_FOUND,
                null
        );
    }

    public NotFoundUserStatusException(UUID userId) {
        super(
                ErrorCode.USER_STATUS_NOT_FOUND.getMessage() + " 사용자 id: " + userId,
                ErrorCode.USER_STATUS_NOT_FOUND,
                Map.of("userId", userId)
        );
    }
}
