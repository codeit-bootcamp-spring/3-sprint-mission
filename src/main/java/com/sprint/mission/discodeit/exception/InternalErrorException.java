package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.dto.ErrorCode;
import com.sprint.mission.discodeit.dto.ResponseMessage;

import java.time.Instant;
import java.util.Map;

public class InternalErrorException extends DiscodeitException {
    public InternalErrorException() {
        super(
                Instant.now(),
                ErrorCode.INTERNAL_ERROR,
                Map.of("message", ResponseMessage.INTERNAL_SERVER_ERROR)
        );
    }
}
