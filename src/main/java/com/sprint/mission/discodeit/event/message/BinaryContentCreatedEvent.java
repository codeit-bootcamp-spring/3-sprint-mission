package com.sprint.mission.discodeit.event.message;

import com.sprint.mission.discodeit.entity.BinaryContent;
import java.time.Instant;
import lombok.Getter;

@Getter
public class BinaryContentCreatedEvent {

    private final BinaryContent data;
    private final Instant createdAt;
    private final byte[] bytes;

    public BinaryContentCreatedEvent(BinaryContent data, Instant createdAt, byte[] bytes) {
        this.data = data;
        this.createdAt = createdAt;
        this.bytes = bytes;
    }
}
