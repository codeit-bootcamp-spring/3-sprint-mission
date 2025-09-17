package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import java.util.Set;
import java.util.UUID;

public record NotificationsCreatedEvent(
    Set<UUID> receiverIds,
    NotificationDto notificationDto

) { }
