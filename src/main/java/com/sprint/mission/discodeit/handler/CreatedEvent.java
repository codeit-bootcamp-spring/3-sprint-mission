package com.sprint.mission.discodeit.handler;

import lombok.Getter;

import java.time.Instant;

/**
 * PackageName  : com.sprint.mission.discodeit.handler
 * FileName     : CreatedEvent
 * Author       : dounguk
 * Date         : 2025. 9. 1.
 */
@Getter
public class CreatedEvent<T> {

    private final T data;
    private final Instant createdAt;

    protected CreatedEvent(final T data, final Instant createdAt) {
        this.data = data;
        this.createdAt = createdAt;
    }
}
