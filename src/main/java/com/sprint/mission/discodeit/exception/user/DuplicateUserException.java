package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class DuplicateUserException extends UserException {

    public DuplicateUserException(String username) {
        super(ErrorCode.DUPLICATE_USER, Map.of("username", username));
    }

}

