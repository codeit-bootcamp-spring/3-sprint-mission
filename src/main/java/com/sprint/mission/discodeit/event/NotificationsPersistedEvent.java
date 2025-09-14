package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.dto.response.NotificationDto;
import java.util.List;
import java.util.UUID;

public record NotificationsPersistedEvent(
    List<UUID> receiverIds,
    List<NotificationDto> notifications
) {}