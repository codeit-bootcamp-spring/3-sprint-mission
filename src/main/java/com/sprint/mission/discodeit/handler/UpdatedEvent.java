package com.sprint.mission.discodeit.handler;

import lombok.Getter;

import java.time.Instant;

/**
 * PackageName  : com.sprint.mission.discodeit.handler
 * FileName     : UpdateEvent
 * Author       : dounguk
 * Date         : 2025. 9. 1.
 */
@Getter
public abstract class UpdatedEvent<T> {

    private final T from;
    private final T to;
    private final Instant updatedAt;

    protected UpdatedEvent(final T from, final T to, final Instant updatedAt) {
        this.from = from;
        this.to = to;
        this.updatedAt = updatedAt;
    }
}