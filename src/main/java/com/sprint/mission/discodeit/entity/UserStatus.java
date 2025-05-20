package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatus implements Serializable {
    private static final long serialVersionUID = 6919471281193075220L;
    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;
    //
    private UUID userId;
    private Instant lastActiveAt;
    //
    public UserStatus(UUID userId, Instant lastActiveAt) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        //
        this.userId = userId;
        this.lastActiveAt = lastActiveAt;
    }
    public void update(Instant newLastActiveAt) {
        this.lastActiveAt = newLastActiveAt;
    }

    public boolean isOnline(){
        int timeAllowed = 300;
        return Instant.now().minusSeconds(timeAllowed).isBefore(this.lastActiveAt);
    }


    @Override
    public String toString() {
        return "UserStatus{" +
                "id=" + id +
                ", userId=" + userId +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
