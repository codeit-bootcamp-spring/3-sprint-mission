package com.sprint.mission.discodeit.exception.auth;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class CustomAccessDeniedException extends AuthException {

    public CustomAccessDeniedException() {
        super(ErrorCode.ACCESS_DENIED);
    }
}
