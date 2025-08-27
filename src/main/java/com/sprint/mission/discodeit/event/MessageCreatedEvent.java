package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.Message;

import java.time.Instant;

public record MessageCreatedEvent(
        Message message,
        Instant occurredAt
) {
}
