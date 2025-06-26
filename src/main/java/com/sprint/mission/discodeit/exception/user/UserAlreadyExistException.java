package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class UserAlreadyExistException extends UserException {

    public UserAlreadyExistException(String field, String value) {
        super(
            field.equals("username") ? ErrorCode.DUPLICATE_USER_NAME
                : ErrorCode.DUPLICATE_USER_EMAIL,
            Map.of(field, value)
        );
    }
}