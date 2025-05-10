package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;

@Getter
public class ReadStatus implements Serializable {
    private static final long serialVersionUID = 8045686006427993964L;
    private Instant cratedAt;
    private Instant updatedAt;

    private UUID id;
    private UUID userId;
    private UUID channelId;

    private Instant lastReadAt;


    public ReadStatus(UUID userId, UUID channelId, Instant lastReadAt) {
        this.cratedAt = Instant.now();
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.channelId = channelId;
        this.lastReadAt = lastReadAt;
    }

    @Override
    public String toString() {
        return "ReadStatus{" +
                "cratedAt=" + cratedAt +
                ", updatedAt=" + updatedAt +
                ", id=" + id +
                ", id=" + userId +
                ", channelId=" + channelId +
                ", lastReadAt=" + lastReadAt +
                '}';
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
