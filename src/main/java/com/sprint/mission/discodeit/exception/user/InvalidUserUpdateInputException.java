package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class InvalidUserUpdateInputException extends UserException {

    public InvalidUserUpdateInputException(String newUsername) {
        super(ErrorCode.INVALID_USER_UPDATE_INPUT, Map.of("newUsername", newUsername));
    }
}
