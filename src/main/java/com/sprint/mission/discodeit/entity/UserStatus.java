package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class UserStatus implements Serializable {
    private static final long serialVersionUID = 1L;
    private UUID id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    //
    private UUID userId;
    private LocalDateTime lastActiveAt;

    public UserStatus(UUID userId, LocalDateTime lastActiveAt) {
        this.id = UUID.randomUUID();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
        //
        this.userId = userId;
        this.lastActiveAt = lastActiveAt;
    }

    public void update(LocalDateTime lastActiveAt) {
        boolean anyValueUpdated = false;
        if (lastActiveAt != null && !lastActiveAt.equals(this.lastActiveAt)) {
            this.lastActiveAt = lastActiveAt;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.updatedAt = LocalDateTime.now();
        }
    }

    public Boolean isOnline() {
        LocalDateTime fiveMinutesAgo = LocalDateTime.now().minus(Duration.ofMinutes(5));

        return lastActiveAt.isAfter(fiveMinutesAgo);
    }
}
