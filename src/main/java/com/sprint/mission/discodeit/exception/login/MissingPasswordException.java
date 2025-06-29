package com.sprint.mission.discodeit.exception.login;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class MissingPasswordException extends LoginException {

    public MissingPasswordException(String password) {
        super(ErrorCode.MISSING_PASSWORD, Map.of("password", password));
    }

}
