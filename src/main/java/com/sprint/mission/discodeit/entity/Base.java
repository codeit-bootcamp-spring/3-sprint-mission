package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

public class Base implements Serializable {
    private UUID id;
    private Long createdAt;
    private Long updatedAt; // private를 안해도 되는가?

    public Base() {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now().getEpochSecond();
    }

    public UUID getId() { // 이게 왜 이렇게 문제가 많이 일어나지
        return id;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void updateUpdatedAt(Long updatedAt) {}
}