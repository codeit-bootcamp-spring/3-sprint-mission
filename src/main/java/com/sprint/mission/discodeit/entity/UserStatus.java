package com.sprint.mission.discodeit.entity;

import java.time.Instant;
import java.util.UUID;

public class UserStatus {
    private static final long serialVersionUID = 6919471281193075220L;
    UUID id;
    UUID userId;
    Instant createdAt;
    Instant updatedAt;
    public UserStatus(UUID userId) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.createdAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }
    public UUID getUserId() {
        return userId;
    }
    public Instant getCreatedAt() {
        return createdAt;
    }
    public Instant getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public String toString() {
        return "UserStatus{" +
                "id=" + id +
                ", userId=" + userId +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
