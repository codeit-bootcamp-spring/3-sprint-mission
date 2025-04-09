package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Common {
    private UUID id;
    private Long createdAt;
    private Long updatedAt;

    public Common() {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void updateUpdatedAt() {
        this.updatedAt = System.currentTimeMillis();
    }
}
