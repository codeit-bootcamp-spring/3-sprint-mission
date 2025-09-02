package com.sprint.mission.discodeit.exception.notification;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class NotificationNotFoundException extends NotificationException {

  public NotificationNotFoundException(String id) {
    super(ErrorCode.NOTIFICATION_NOT_FOUND, "알림을 찾을 수 없습니다. [id: " + id + "]");
  }
}
