package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class DuplicateUserException extends UserException {

    public DuplicateUserException(String username, String email) {
        super(ErrorCode.DUPLICATE_BOTH, Map.of("username", username, "email", email));
    }
}
