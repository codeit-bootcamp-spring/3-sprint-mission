package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class UserNameAlreadyExistsException extends UserException{

    public UserNameAlreadyExistsException() {
        super(ErrorCode.DUPLICATE_USERNAME);
    }

    public UserNameAlreadyExistsException(Map<String, Object> details) {
        super(ErrorCode.DUPLICATE_USERNAME, details);
    }
}
