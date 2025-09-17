package com.sprint.mission.discodeit.dto.data;

import lombok.Getter;

import java.util.UUID;

@Getter
public class SseMessage {

    private final UUID id;
    private final String eventName;
    private final Object data;

    public SseMessage(UUID id, String eventName, Object data) {
        this.id = id;
        this.eventName = eventName;
        this.data = data;
    }


}
