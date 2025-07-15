package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.dto.ErrorCode;
import com.sprint.mission.discodeit.dto.ResponseMessage;

import java.time.Instant;
import java.util.Map;

public class IdOrPasswordValidException extends DiscodeitException {
    public IdOrPasswordValidException() {
        super(
                Instant.now(),
                ErrorCode.ID_OR_PASSWORD_VALID,
                Map.of("message", ResponseMessage.ID_OR_PASSWORD_VALID)
        );
    }
}
