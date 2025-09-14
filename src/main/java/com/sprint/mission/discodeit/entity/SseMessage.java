package com.sprint.mission.discodeit.entity;

import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SseMessage {

    private final UUID id;
    private final String name;
    private final Object data;
    private final Instant createdAt;

    @Override
    public String toString() {
        return "SseMessage{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", data=" + data +
            ", createdAt=" + createdAt +
            '}';
    }
}
