package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.entity.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class NotFoundUserException extends UserException {

    public NotFoundUserException(UUID userId) {
        super(
                ErrorCode.USER_NOT_FOUND.getMessage() + " userId: " + userId,
                ErrorCode.USER_NOT_FOUND,
                Map.of("userId", userId)
        );
    }

    public NotFoundUserException(String username) {
        super(
                ErrorCode.USER_NOT_FOUND.getMessage() + " username: " + username,
                ErrorCode.USER_NOT_FOUND,
                Map.of("username", username)
        );
    }
}
