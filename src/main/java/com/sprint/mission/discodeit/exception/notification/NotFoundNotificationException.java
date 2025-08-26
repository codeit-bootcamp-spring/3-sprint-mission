package com.sprint.mission.discodeit.exception.notification;

import com.sprint.mission.discodeit.entity.enums.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class NotFoundNotificationException extends NotificationException {

    public NotFoundNotificationException(UUID notificationId) {
        super(
            ErrorCode.NOTIFICATION_NOT_FOUND.getMessage() + " notificationId: + " + notificationId,
            ErrorCode.NOTIFICATION_NOT_FOUND,
            Map.of("notificationId", notificationId)
        );
    }
}
