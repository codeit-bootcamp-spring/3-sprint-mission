package com.sprint.mission.discodeit.exception.message;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

public class MessageException extends DiscodeitException {
    public MessageException(ErrorCode code, Object details) {
        super(code, details);
    }
}
