package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;

@Getter
public class UserStatus implements Serializable {
    private static final long serialVersionUID = -7488891392699063722L;
    private Instant cratedAt;
    private Instant updatedAt;
    private Instant StatusAt;

    private UUID id;
    private UUID userId;

    public UserStatus(Instant recentStatusAt, UUID userId) {
        this.cratedAt = Instant.now();
        this.StatusAt = recentStatusAt;
        this.updatedAt = Instant.now();
        this.id = UUID.randomUUID();
        this.userId = userId;
    }

    public void update(Instant recentStatusAt) {
        boolean anyValueUpdated = false;

        if (recentStatusAt != null && !recentStatusAt.equals(this.StatusAt)) {
            this.StatusAt = recentStatusAt;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.updatedAt = Instant.now();
        }
    }

    @Override
    public String toString() {
        return "UserStatus{" +
                "cratedAt=" + cratedAt +
                ", updatedAt=" + updatedAt +
                ", id=" + id +
                ", id=" + userId +
                '}';
    }

}
