package com.sprint.mission.discodeit.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Getter
@ToString
public class UserStatus implements Serializable {

    private static final long serialVersionUID = -8208733885105787316L;

    /* 공통 필드 */
    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;

    private UUID userId;                // 유저ID
    private Instant lastActiveAt;     // 마지막으로 확인된 접속시간

    @Builder
    public UserStatus(UUID userId, Instant lastActiveAt) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();

        this.userId = userId;
        this.lastActiveAt = lastActiveAt;
    }

    public void update(Instant lastActiveAt) {
        this.updatedAt = Instant.now();
        this.lastActiveAt = lastActiveAt != null ? lastActiveAt : Instant.now();
    }

    public boolean isOnline() {

        Instant instantFiveMinuteAgo = Instant.now().minus(Duration.ofMinutes(5));

        return this.lastActiveAt.isAfter(instantFiveMinuteAgo);
    }
}
