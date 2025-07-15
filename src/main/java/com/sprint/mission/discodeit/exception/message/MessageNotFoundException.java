package com.sprint.mission.discodeit.exception.message;

import com.sprint.mission.discodeit.dto.ErrorCode;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class MessageNotFoundException extends MessageException {
    public MessageNotFoundException(UUID messageId) {
        super(
                Instant.now(),
                ErrorCode.MESSAGE_NOT_FOUND,
                Map.of("messageId", messageId)
        );
    }
}
