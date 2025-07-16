package com.sprint.mission.discodeit.exception.login;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class UserNotFoundForLoginException extends LoginException {

    public UserNotFoundForLoginException(String username) {
        super(ErrorCode.USER_NOT_FOUND, Map.of("username", username));
    }

}
