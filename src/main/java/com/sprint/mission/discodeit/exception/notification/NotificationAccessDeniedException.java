package com.sprint.mission.discodeit.exception.notification;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public class NotificationAccessDeniedException extends DiscodeitException {

    public NotificationAccessDeniedException() {
        super(ErrorCode.NOTIFICATION_ACCESS_DENIED);
    }
}
