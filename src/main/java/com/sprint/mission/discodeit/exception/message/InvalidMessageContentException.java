package com.sprint.mission.discodeit.exception.message;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class InvalidMessageContentException extends MessageException {

    public InvalidMessageContentException(String content) {
        super(ErrorCode.INVALID_MESSAGE_CONTENT, Map.of("content", content));
    }
}
