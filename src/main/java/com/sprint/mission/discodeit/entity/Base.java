package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Base {
    private UUID id; // Channel CRUD를 위해 UUID가 필요한가?
    // @CreatedDate 는 Spring 전용
    private Long createdAt;
    // @CreatedDate
    private Long updatedAt;

    public Base() {
    }

    public Base(UUID id, Long createdAt, Long updatedAt) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = createdAt;
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

    public void updateUpdatedAt() {
        this.updatedAt = System.currentTimeMillis();
}
