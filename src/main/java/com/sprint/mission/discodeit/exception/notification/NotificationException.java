package com.sprint.mission.discodeit.exception.notification;

import com.sprint.mission.discodeit.dto.response.ErrorCode;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import java.time.Instant;
import java.util.Map;

public class NotificationException extends DiscodeitException {

    protected NotificationException(Instant timestamp, ErrorCode errorCode, Map<String, Object> details) {
        super(timestamp, errorCode, details);
    }
}
