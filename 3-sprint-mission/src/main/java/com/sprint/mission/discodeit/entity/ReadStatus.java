package com.sprint.mission.discodeit.entity;

import lombok.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@ToString
public class ReadStatus implements Serializable {
    private final UUID id;
    private UUID userId;
    private UUID channelId;
    private final Instant createdAt;
    private Instant updatedAt;
    private Instant lastReadAt;

    @Builder
    public ReadStatus(UUID userId, UUID channelId, Instant lastReadAt) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.channelId = channelId;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.lastReadAt = lastReadAt;

    }

    public void update(Instant newLastReadAt) {
        boolean anyValueUpdated = false;
        if (newLastReadAt != null && !newLastReadAt.equals(this.lastReadAt)) {
            this.lastReadAt = newLastReadAt;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.updatedAt = Instant.now();
        }
    }

}
