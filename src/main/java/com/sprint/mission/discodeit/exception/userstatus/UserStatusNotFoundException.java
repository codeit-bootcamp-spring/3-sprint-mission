package com.sprint.mission.discodeit.exception.userstatus;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class UserStatusNotFoundException extends UserStatusException {

    public UserStatusNotFoundException(UUID id) {
        super(ErrorCode.USER_STATUS_NOT_FOUND, Map.of("userStatusId", id.toString()));
    }

    public static UserStatusNotFoundException byUserId(UUID userId) {
        return new UserStatusNotFoundException(ErrorCode.USER_STATUS_NOT_FOUND,
            Map.of("userId", userId.toString()));
    }

    private UserStatusNotFoundException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }
}
