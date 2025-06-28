package com.sprint.mission.discodeit.exception.User;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;

@Getter
public class UserAlreadyExistsException extends UserException {

    private final UUID userId;
    private final String userName;
    private final String email;

    public UserAlreadyExistsException(UUID userId, String userName, String email) {
        super(
                ErrorCode.DUPLICATE_USER,
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

    public static UserAlreadyExistsException byId(UUID userId) {
        return new UserAlreadyExistsException(userId, null, null);
    }

    public static UserAlreadyExistsException byUserName(String userName) {
        return new UserAlreadyExistsException(null, userName, null);
    }

    public static UserAlreadyExistsException byEmail(String email) {
        return new UserAlreadyExistsException(null, null, email);
    }
}
