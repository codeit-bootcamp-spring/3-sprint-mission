package com.sprint.mission.discodeit.entitiy;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class UserStatus {
    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;
    private UUID userId;
    private Instant lastSeen;

    public UserStatus(UUID userId) {
        this.userId = userId;
        this.id = UUID.randomUUID();
        this.lastSeen = Instant.now();
        this.createdAt = Instant.now();
    }

    public Boolean IsOnline(){
        Instant now = Instant.now();
        if(now.minusSeconds(300).isAfter(lastSeen)){
            return false;
        }else{
            return true;
        }
    }

    @Override
    public String toString() {
        return "UserStatus{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", userId=" + userId +
                ", lastSeen=" + lastSeen +
                '}';
    }
}
