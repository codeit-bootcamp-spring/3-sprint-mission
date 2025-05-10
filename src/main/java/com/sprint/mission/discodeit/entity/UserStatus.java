package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
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
    private Instant lastAccessedAt;     // 마지막으로 확인된 접속시간

    public UserStatus(UUID userId) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();

        this.userId = userId;
        this.lastAccessedAt = Instant.now();
    }

    public void update(Instant lastAccessedAt) {
        this.updatedAt = Instant.now();
        this.lastAccessedAt = lastAccessedAt != null ? lastAccessedAt : Instant.now();
    }

    public boolean isOnline() {
        return this.lastAccessedAt.isAfter(Instant.now().minusSeconds(300));
    }
}
