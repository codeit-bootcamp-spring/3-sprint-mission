package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.Channel;
import java.util.UUID;

public class ChannelEvent extends BaseEvent<Channel> {

    public ChannelEvent(UUID id, Channel channel, EventType type) {
        super(id, channel, type);
    }
}