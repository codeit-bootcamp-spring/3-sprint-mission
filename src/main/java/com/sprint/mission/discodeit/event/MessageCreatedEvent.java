package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.UUID;

public record MessageCreatedEvent(
        UUID messageId,
        UUID channelId,
        UUID authorId,
        String authorUsername,
        String content,
        ChannelType channelType,
        String channelName
) {

}