package com.sprint.mission.discodeit.event;

import java.util.UUID;

public record MessageCreatedEvent(
    UUID id,
    UUID channelId,
    UUID authorId,
    String content
) {
}
