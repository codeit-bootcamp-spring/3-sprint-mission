package com.sprint.mission.discodeit.entity;

import java.util.UUID;
import lombok.Getter;

@Getter
public class SseMessage {

    private final UUID id;
    private final String eventName;
    private final Object data;

    public SseMessage(String eventName, Object data) {
        this.id = UUID.randomUUID();
        this.eventName = eventName;
        this.data = data;
    }

}
