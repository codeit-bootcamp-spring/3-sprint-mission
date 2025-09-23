package com.sprint.mission.discodeit.exception.notification;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class NotificationNotFoundException extends NotificationException {

    public NotificationNotFoundException(String message) {
        super(ErrorCode.NOTIFICATION_NOT_FOUND, Map.of("message", message));
    }
}
