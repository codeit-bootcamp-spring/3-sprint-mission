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
    private Instant lastActiveTime;
    private final Instant createdAt;
    private Instant updatedAt;

    @Builder
    public UserStatus(UUID userId, Instant lastActiveTime) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.lastActiveTime = lastActiveTime;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public void update(Instant newActiveTime) {
        this.lastActiveTime = newActiveTime;
    }

    public Boolean online() {
        Boolean online =
                this.lastActiveTime.isAfter(Instant.now().minus(5, ChronoUnit.MINUTES));
        return online;

    }

}
