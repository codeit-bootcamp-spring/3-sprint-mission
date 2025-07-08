package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.entity.ErrorCode;

import java.util.Map;

public class DuplicateNameException extends UserException {

    public DuplicateNameException(String username) {
        super(
                ErrorCode.DUPLICATE_USER_NAME.getMessage() + " name: " + username,
                ErrorCode.DUPLICATE_USER_NAME,
                Map.of("username", username)
        );
    }
}
