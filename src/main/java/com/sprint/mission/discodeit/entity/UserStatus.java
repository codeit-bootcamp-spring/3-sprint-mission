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
    //
    public UserStatus(UUID userId) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        //
        this.userId = userId;
    }
    public void refresh() {
        this.updatedAt = Instant.now();
    }
    public boolean isOnline(){
        int timeAllowed = 300;
        boolean isOnline = false;
        if(Instant.now().minusSeconds(timeAllowed).isBefore(this.updatedAt)) {
            isOnline = true;
        }
        return isOnline;
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
