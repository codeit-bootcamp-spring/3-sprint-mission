package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.Message;
import java.util.UUID;
import lombok.Getter;

@Getter
public class MessageEvent extends BaseEvent<Message> {

    private final UUID channelId;

    public MessageEvent(UUID id, Message message, UUID channelId, EventType type) {
        super(id, message, type);
        this.channelId = channelId;
    }
}