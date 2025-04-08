package com.sprint.mission.discodeit.entity;

import java.util.UUID;

// 공통으로 들어갈 변수들
public abstract class BaseEntity {
    private final UUID id;
    private final Long createdAt;
    private Long updatedAt;

    public BaseEntity() {
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

    public void updateTime() {
        this.updatedAt = System.currentTimeMillis();
    }
}
