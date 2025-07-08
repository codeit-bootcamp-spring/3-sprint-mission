package com.sprint.mission.discodeit.exception.login;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class LoginException extends DiscodeitException {

    public LoginException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }
}
