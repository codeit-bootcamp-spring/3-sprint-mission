package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;

@Getter
public class UserStatus implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;
    //
    private UUID userId;
    private Instant lastActiveAt;

    public UserStatus(UUID userId, Instant lastActiveAt) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        //
        this.userId = userId;
        this.lastActiveAt = lastActiveAt;
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

    /**
     * 마지막 접속 시간이 현재 시간 기준 5분 이내이면 true
     */
    public Boolean isOnline() {
        Instant instantFiveMinutesAgo = Instant.now().minus(Duration.ofMinutes(5));

        return lastActiveAt.isAfter(instantFiveMinutesAgo);
    }
}
