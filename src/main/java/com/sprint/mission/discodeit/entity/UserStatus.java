package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatus implements Serializable {
    private final UUID id;
    private final UUID userId;
    private final Instant createdAt;
    private Instant updatedAt;
    private static final int LOGIN_TIMEOUT_SECONDS = 300;

    @Serial
    private static final long serialVersionUID = -412161012207255446L;

    private UserStatus(UUID userId) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;;
    }

    public static UserStatus of(UUID userId) {
        return new UserStatus(userId);
    }

    public UserStatus update() {
        this.updatedAt = Instant.now();
        return this;
    }

    public boolean isLoggedIn() {
        if (updatedAt.equals(createdAt)) {
            return false;
        }
        return Duration.between(updatedAt, Instant.now()).toSeconds() <= LOGIN_TIMEOUT_SECONDS;
    }

    @Override
    public String toString() {
        return "[UserStatus] {id=" + id + ", userId=" + userId + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + "}";
    }
}
