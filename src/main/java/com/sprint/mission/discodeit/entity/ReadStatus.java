package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

public class ReadStatus {
    @Getter
    private final UUID id;
    @Getter
    private final Instant createdAt;
    @Getter
    private Instant updatedAt;
    //
    @Getter
    private final UUID userId;
    @Getter
    private final UUID channelId;
    //
    @Getter
    private Boolean isRead;
    @Getter
    private Instant lastReadTime;


    public ReadStatus(UUID userId, UUID channelId) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.isRead = false;
        this.lastReadTime = Instant.now();

        //
        this.userId = userId;
        this.channelId = channelId;
    }
}
