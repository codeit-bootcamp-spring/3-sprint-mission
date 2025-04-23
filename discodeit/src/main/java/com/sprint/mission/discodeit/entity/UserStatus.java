package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;


@Getter
public class UserStatus {
    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;

    private UUID userId;
    private Instant lastActiveAt;

    public UserStatus(UUID userId, Instant lastActiveAt) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.userId = userId;
        this.lastActiveAt = lastActiveAt;
    }

    public void update(Instant lastActiveAt) {
        if(lastActiveAt != null && !lastActiveAt.equals(this.lastActiveAt)){
            this.lastActiveAt = lastActiveAt;
            this.updatedAt = Instant.now();
        }
    }


    /*마지막 접속이 현재시간기점으로 5분이 지났으면 true를 아니면 false를 리턴*/
    public boolean isActive() {
        return lastActiveAt.isAfter(Instant.now().minus(Duration.ofMinutes(5)));
    }


}
