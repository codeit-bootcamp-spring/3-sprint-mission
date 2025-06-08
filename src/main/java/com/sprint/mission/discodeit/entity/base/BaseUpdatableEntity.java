package com.sprint.mission.discodeit.entity.base;

import java.time.Instant;

public abstract class BaseUpdatableEntity extends BaseEntity {
    protected Instant createdAt;
    protected Instant updatedAt;

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}