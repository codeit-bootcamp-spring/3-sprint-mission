package com.sprint.mission.discodeit.exception.notification;

import com.sprint.mission.discodeit.entity.enums.ErrorCode;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import java.util.Map;

public class NotificationException extends DiscodeitException {

    public NotificationException(String message, ErrorCode errorCode, Map<String, Object> details) {
        super(message, errorCode, details);
    }
}
