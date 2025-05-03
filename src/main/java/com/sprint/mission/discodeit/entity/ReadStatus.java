package com.sprint.mission.discodeit.entity;

import java.time.Instant;
import java.util.UUID;
import lombok.Getter;

public class ReadStatus {
    private static final long serialVersionUID = 1L;

    @Getter
    private UUID id;

    @Getter
    private Instant createdAt;

    @Getter
    private Instant updatedAt;

    @Getter
    private UUID userId;

    @Getter
    private UUID channelId;

    public ReadStatus(UUID userId, UUID channelId) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        //
        this.userId = userId;
        this.channelId = channelId;
    }
}
