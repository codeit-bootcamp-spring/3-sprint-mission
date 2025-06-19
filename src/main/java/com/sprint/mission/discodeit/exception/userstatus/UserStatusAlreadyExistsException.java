package com.sprint.mission.discodeit.exception.userstatus;

import com.sprint.mission.discodeit.entity.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class UserStatusAlreadyExistsException extends UserStatusException {

    public UserStatusAlreadyExistsException(UUID userId) {
        super(
                ErrorCode.USER_STATUS_ALREADY_EXIST.getMessage() + "사용자 id: " + userId,
                ErrorCode.USER_STATUS_ALREADY_EXIST,
                Map.of("userId", userId)
        );
    }
}
