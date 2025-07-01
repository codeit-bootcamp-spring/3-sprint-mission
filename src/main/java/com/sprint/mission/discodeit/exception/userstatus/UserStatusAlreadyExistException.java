package com.sprint.mission.discodeit.exception.userstatus;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.UUID;

public class UserStatusAlreadyExistException extends UserStatusException {

    public UserStatusAlreadyExistException() {
        super(ErrorCode.USER_STATUS_ALREADY_EXISTS);
    }

    public static UserStatusAlreadyExistException withUserId(UUID userId) {
        UserStatusAlreadyExistException exception = new UserStatusAlreadyExistException();
        exception.addDetail("userId", userId);
        return exception;
    }
} 