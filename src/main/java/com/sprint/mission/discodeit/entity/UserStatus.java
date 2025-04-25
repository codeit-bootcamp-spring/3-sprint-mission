package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

public class UserStatus {
    @Getter
    private final UUID id;
    @Getter
    private final Instant createdAt;
    @Getter
    private Instant updatedAt;
    //
    @Getter
    private final UUID userId;
    //
    @Getter
    private UserStatusType status;
    @Getter
    private Instant lastOnlineTime;


    public UserStatus(UUID userId) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.status = UserStatusType.ONLINE;
        this.lastOnlineTime = Instant.now();
        //
        this.userId = userId;
    }

}
