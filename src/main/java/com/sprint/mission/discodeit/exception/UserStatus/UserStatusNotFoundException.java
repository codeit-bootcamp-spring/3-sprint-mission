package com.sprint.mission.discodeit.exception.UserStatus;

import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.User.UserNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;

@Getter
public class UserStatusNotFoundException extends UserStatusException {

    private final UUID userStatusId;
    private final UUID userId;

    public UserStatusNotFoundException(UUID userStatusId, UUID userId) {
        super(
                ErrorCode.USER_STATUS_NOT_FOUND,
                buildDetails(userStatusId, userId)
        );
        this.userStatusId = userStatusId;
        this.userId = userId;
    }

    private static Map<String, Object> buildDetails(UUID userStatusId, UUID userId) {
        Map<String, Object> details = new HashMap<>();

        if (userStatusId != null) {
            details.put("userStatusId", userStatusId);
        }
        if (userId != null) {
            details.put("userId", userId);
        }

        return details;
    }

    public static UserStatusNotFoundException byId(UUID userStatusId) {
        return new UserStatusNotFoundException(userStatusId, null);
    }

    public static UserStatusNotFoundException byUserId(UUID userId) {
        return new UserStatusNotFoundException(null, userId);
    }
}
