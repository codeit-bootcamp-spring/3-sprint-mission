package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.dto.response.MessageDto;
import java.time.Instant;

public record MessageCreatedEvent(
    MessageDto messageDto,
    Instant occurredAt
) {
    public static MessageCreatedEvent now(MessageDto messageDto) {
        return new MessageCreatedEvent(messageDto, Instant.now());
    }
}
