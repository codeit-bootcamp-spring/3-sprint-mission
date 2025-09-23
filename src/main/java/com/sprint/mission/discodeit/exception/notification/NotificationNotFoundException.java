package com.sprint.mission.discodeit.exception.notification;

import com.sprint.mission.discodeit.dto.ErrorCode;
import com.sprint.mission.discodeit.exception.DiscodeitException;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class NotificationNotFoundException extends DiscodeitException {
  public NotificationNotFoundException(UUID notificationId) {
    super(
            Instant.now(),
            ErrorCode.NOTIFICATION_NOT_FOUND,
            Map.of("notificationId", notificationId)
    );
  }
}
