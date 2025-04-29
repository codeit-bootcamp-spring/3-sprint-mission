package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatus implements Serializable {

    @Serial
    private static final long serialVersionUID = -8852497287437352577L;

    private final UUID id;
    private final Instant createdAt;
    private Instant updatedAt;

    private final UUID userId;
    private Instant accessedAt;

    public UserStatus(UUID userid, Instant accessedAt) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = createdAt;
        this.userId = userid;
        this.accessedAt = accessedAt;
    }

    public void updateTime() {
        this.updatedAt = Instant.now();
    }

    public void updateAccessedTime(Instant accessedAt) {
        this.accessedAt = accessedAt;
        updateTime();
    }

    public boolean isOnline() {
        return Duration.between(accessedAt, Instant.now()).toMinutes() < 5;
    }

    @Override
    public String toString() {
        return "UserStatus{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", userId=" + userId +
                ", accessedAt=" + accessedAt +
                '}';
    }
}
