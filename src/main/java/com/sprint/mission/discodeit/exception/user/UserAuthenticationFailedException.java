package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class UserAuthenticationFailedException extends UserException {

    public UserAuthenticationFailedException() {
        super(ErrorCode.USER_WRONG_PASSWORD);
    }

    public static UserAuthenticationFailedException withPassword() {
        UserAuthenticationFailedException exception = new UserAuthenticationFailedException();
        return exception;
    }
} 