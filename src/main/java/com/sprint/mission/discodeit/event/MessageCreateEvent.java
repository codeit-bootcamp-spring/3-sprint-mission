package com.sprint.mission.discodeit.event;

import java.util.UUID;

public record MessageCreateEvent(
    UUID messageId,
    UUID channelId,
    UUID authorId,
    String authorUsername,
    String channelName,
    String content
) {

}
