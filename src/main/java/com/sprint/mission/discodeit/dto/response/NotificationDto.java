package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.Notification;
import java.time.Instant;
import java.util.UUID;

public record NotificationDto(
    UUID id,
    UUID receiverId,
    String title,
    String content,
    Instant createdAt
) {

  public static NotificationDto from(Notification notification) {
    return new NotificationDto(
        notification.getId(),
        notification.getReceiver().getId(),
        notification.getTitle(),
        notification.getContent(),
        notification.getCreatedAt()
    );
  }
}
