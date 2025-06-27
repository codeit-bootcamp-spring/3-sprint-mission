package com.sprint.mission.discodeit.exception.message;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class MessageTooLongException extends MessageException {

    public MessageTooLongException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }
}
