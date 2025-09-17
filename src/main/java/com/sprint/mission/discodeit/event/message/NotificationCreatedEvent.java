package com.sprint.mission.discodeit.event.message;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;

@Getter
public class NotificationCreatedEvent extends CreatedEvent<NotificationDto>{

    private final Set<UUID> receiverIds;

    public NotificationCreatedEvent(Set<UUID> receiverIds,NotificationDto data, Instant createdAt) {
        super(data, createdAt);
        this.receiverIds = receiverIds;
    }

}
