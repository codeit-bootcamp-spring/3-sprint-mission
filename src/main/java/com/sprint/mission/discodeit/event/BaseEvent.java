package com.sprint.mission.discodeit.event;

import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public abstract class BaseEvent<T> {

    private final UUID id;
    private final T entity;
    private final EventType type;
    private final Instant occurredAt;

    protected BaseEvent(UUID id, T entity, EventType type) {
        this.id = id;
        this.entity = entity;
        this.type = type;
        this.occurredAt = Instant.now();
    }


}
