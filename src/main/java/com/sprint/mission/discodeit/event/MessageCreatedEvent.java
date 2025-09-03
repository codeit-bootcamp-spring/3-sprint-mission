package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.UUID;

public record MessageCreatedEvent (
        UUID channelId,
        UUID messageId,
        UUID authorId,
        String authorUsername,
        String channelName,
        ChannelType channelType,
        String content
){
}
