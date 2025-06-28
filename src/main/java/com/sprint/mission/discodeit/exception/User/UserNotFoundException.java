package com.sprint.mission.discodeit.exception.User;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;

@Getter
public class UserNotFoundException extends UserException {

    private final UUID userId;
    private final String userName;
    private final String email;

    public UserNotFoundException(UUID userId, String userName, String email) {
        super(
                ErrorCode.USER_NOT_FOUND,
                buildDetails(userId, userName, email)
        );
        this.userId = userId;
        this.userName = userName;
        this.email = email;
    }

    private static Map<String, Object> buildDetails(UUID userId, String userName, String email) {
        Map<String, Object> details = new HashMap<>();

        if (userId != null) {
            details.put("userId", userId);
        }
        if (userName != null) {
            details.put("userName", userName);
        }
        if (email != null) {
            details.put("email", email);
        }

        return details;
    }

    public static UserNotFoundException byId(UUID userId) {
        return new UserNotFoundException(userId, null, null);
    }

    public static UserNotFoundException byUserName(String userName) {
        return new UserNotFoundException(null, userName, null);
    }

    public static UserNotFoundException byEmail(String email) {
        return new UserNotFoundException(null, null, email);
    }
}
