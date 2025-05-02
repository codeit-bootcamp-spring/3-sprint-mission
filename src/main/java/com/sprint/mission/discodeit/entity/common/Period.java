package com.sprint.mission.discodeit.entity.common;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

public abstract class Period implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final Instant createdAt;
    private Instant updatedAt;

    public Period() {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public Period(UUID id, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void update() {
        this.updatedAt = Instant.now();
    }

    @Override
    public String toString() {
        return "Period{" +
                "createdAt=" + createdAt +
                ", id=" + id +
                ", updatedAt=" + updatedAt +
                '}';
    }
}