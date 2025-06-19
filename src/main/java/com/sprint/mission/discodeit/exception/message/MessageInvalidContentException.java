package com.sprint.mission.discodeit.exception.message;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class MessageInvalidContentException extends MessageException {

    public MessageInvalidContentException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }
}
