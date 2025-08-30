package com.sprint.mission.discodeit.event;

import java.util.UUID;

public record MessageCreatedEvent(
    UUID userId,
    UUID channelId,
    String userName,
    String channelName,
    String messageContent
) { }