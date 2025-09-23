package com.sprint.mission.discodeit.event;

import java.time.Instant;
import java.util.UUID;

public record MessageCreatedEvent(
    UUID messageId,
    UUID authorId,
    String authorName,
    UUID channelId,
    String channelName,
    String content,
    Instant occurredAt
) {

}
