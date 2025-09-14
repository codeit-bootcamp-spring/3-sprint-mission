package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.dto.notification.NotificationDto;
import java.util.UUID;

public record NotificationCreatedEvent(
    UUID receiverId,
    NotificationDto dto
) {

}
