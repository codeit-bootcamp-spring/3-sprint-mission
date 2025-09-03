package com.sprint.mission.discodeit.exception.auth;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

public class InvalidRefreshTokenException extends DiscodeitException {

    public InvalidRefreshTokenException() {
        super(ErrorCode.INVAILD_REFRESH_TOKEN);
    }

    public InvalidRefreshTokenException(String customMessage) {
      super(ErrorCode.INVAILD_REFRESH_TOKEN);
    }
}
