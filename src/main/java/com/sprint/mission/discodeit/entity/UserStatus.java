package com.sprint.mission.discodeit.entity;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;

public class UserStatus {
    private static final long serialVersionUID = 1L;

    @Getter
    private UUID id;

    @Getter
    private Instant createdAt;

    @Getter
    private Instant updatedAt;

    @Getter
    private UUID userId;

    public UserStatus(UUID userId) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        //
        this.userId = userId;
    }

    /**
     * 마지막 접속 시간이 현재 시간 기준 5분 이내이면 true
     */
    public boolean isConnected() {
        Instant now = Instant.now();
        Duration duration = Duration.between(updatedAt, now);
        return duration.toMinutes() < 5;
    }
}
