package com.sprint.mission.discodeit.entity;

import lombok.*;

import java.io.Serializable;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Getter
@ToString
public class UserStatus implements Serializable {
    private final UUID id;
    private final UUID userId;
    private Instant lastActiveAt;
    private final Instant createdAt;
    private Instant updatedAt;

    @Builder
    public UserStatus(UUID userId, Instant lastActiveAt) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.lastActiveAt = lastActiveAt;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public void update(Instant lastActiveAt) {
        boolean anyValueUpdated = false;
        if (lastActiveAt != null && !lastActiveAt.equals(this.lastActiveAt)) {
            this.lastActiveAt = lastActiveAt;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.updatedAt = Instant.now();
        }
    }

    public Boolean online() {
        Boolean online =
                this.lastActiveAt.isAfter(Instant.now().minus(5, ChronoUnit.MINUTES));
        return online;

    }

}
