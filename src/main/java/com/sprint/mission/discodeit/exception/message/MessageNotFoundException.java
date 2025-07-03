package com.sprint.mission.discodeit.exception.message;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class MessageNotFoundException extends MessageException {
    public MessageNotFoundException(Object details) {
        super(ErrorCode.MESSAGE_NOT_FOUND, details);
    }
}
