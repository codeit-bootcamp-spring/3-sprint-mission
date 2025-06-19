package com.sprint.mission.discodeit.exception.message;

import com.sprint.mission.discodeit.entity.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class NotFoundMessageException extends MessageException {

    public NotFoundMessageException(UUID messageId) {
        super(
                ErrorCode.MESSAGE_NOT_FOUND.getMessage() + " messageId: + " + messageId,
                ErrorCode.MESSAGE_NOT_FOUND,
                Map.of("messageId", messageId)
        );
    }
}
