package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class UserNameAlreadyExistsException extends UserException {

    public UserNameAlreadyExistsException(String username) {
        super(ErrorCode.USERNAME_ALREADY_EXISTS, Map.of("username", username));
    }
}
