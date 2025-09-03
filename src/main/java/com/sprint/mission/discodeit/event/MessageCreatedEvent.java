package com.sprint.mission.discodeit.event;

import java.time.Instant;
import java.util.UUID;

public record MessageCreatedEvent(
    UUID messageId,
    UUID channelId,
    String channelName,
    UUID authorId,
    String authorName,
    String content,
    Instant createdAt
) {

}
