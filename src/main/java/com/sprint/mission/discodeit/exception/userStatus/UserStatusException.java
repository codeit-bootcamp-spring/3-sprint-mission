package com.sprint.mission.discodeit.exception.userStatus;

import com.sprint.mission.discodeit.dto.response.ErrorCode;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import java.time.Instant;
import java.util.Map;

public abstract class UserStatusException extends DiscodeitException {

    protected UserStatusException(Instant timestamp, ErrorCode errorCode, Map<String, Object> details) {
        super(timestamp, errorCode, details);
    }
}
