package com.sprint.mission.discodeit.exception.custom.auth;

import com.sprint.mission.discodeit.exception.base.AuthenticationException;

public class InvalidPasswordException extends AuthenticationException {

    public InvalidPasswordException(String message) {
        super(message);
    }
}
