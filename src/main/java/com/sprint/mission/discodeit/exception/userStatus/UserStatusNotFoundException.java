package com.sprint.mission.discodeit.exception.userStatus;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class UserStatusNotFoundException extends UserStatusException {

    private UserStatusNotFoundException(String key, Object value) {
        super(ErrorCode.USER_STATUS_NOT_FOUND, Map.of(key, value));
    }

    public static UserStatusNotFoundException byUserId(UUID userId) {
        return new UserStatusNotFoundException("userId", userId);
    }

    public static UserStatusNotFoundException byUserStatusId(UUID userStatusId) {
        return new UserStatusNotFoundException("userStatusId", userStatusId);
    }
}
