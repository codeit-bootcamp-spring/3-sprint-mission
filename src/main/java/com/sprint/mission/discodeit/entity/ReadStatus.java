package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;

@Getter
public class ReadStatus implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;
    //
    private UUID userId;
    private UUID channelId;
    private Instant lastReadAt;

    public ReadStatus(UUID userId, UUID channelId, Instant lastReadAt) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
        //
        this.userId = Objects.requireNonNull(userId, "User ID must not be null");
        this.channelId = Objects.requireNonNull(channelId, "Channel ID must not be null");
        this.lastReadAt = Objects.requireNonNull(lastReadAt, "Last read time must not be null");
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
