package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.time.Instant;
import java.util.UUID;

@Getter
public class ReadStatus implements java.io.Serializable {

    @Serial
    private static final long serialVersionUID = 4852408247773241680L;

    private final UUID id;
    private final Instant createdAt;
    private Instant updatedAt;

    private final UUID userId;
    private final UUID channelId;
    private Instant readAt;

    public ReadStatus(UUID userId, UUID channelId, Instant readAt) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = createdAt;
        this.userId = userId;
        this.channelId = channelId;
        this.readAt = readAt;
    }

    public void updateTime() {
        this.updatedAt = Instant.now();
    }

    public void updateReadTime(Instant readAt) {
        this.readAt = readAt;
        updateTime();
    }

    @Override
    public String toString() {
        return "ReadStatus{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", userId=" + userId +
                ", channelId=" + channelId +
                ", readAt=" + readAt +
                '}';
    }
}
