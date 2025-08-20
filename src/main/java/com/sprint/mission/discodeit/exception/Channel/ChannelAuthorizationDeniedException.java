package com.sprint.mission.discodeit.exception.Channel;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

public class ChannelAuthorizationDeniedException extends DiscodeitException {

    public ChannelAuthorizationDeniedException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ChannelAuthorizationDeniedException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public ChannelAuthorizationDeniedException(ErrorCode errorCode, String message,
        Throwable cause) {
        super(errorCode, message, cause);
    }
}
