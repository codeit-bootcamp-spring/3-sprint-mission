package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.entity.ErrorCode;

import java.util.Map;

public class LoginFailedException extends UserException {

    public LoginFailedException(String username) {
        super(
                ErrorCode.LOGIN_FAILED.getMessage() + " username: " + username,
                ErrorCode.LOGIN_FAILED,
                Map.of("username", username)
        );
    }
}
