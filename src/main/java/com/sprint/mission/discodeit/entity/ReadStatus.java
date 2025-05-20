package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class ReadStatus implements Serializable {
    private final UUID id;
    private final UUID userId;
    private final UUID channelId;
    private final Instant createdAt;
    private Instant updatedAt;

    @Serial
    private static final long serialVersionUID = -8772820009331216666L;

    private ReadStatus(UUID userId, UUID channelId) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.channelId = channelId;
        this.createdAt = Instant.now();
        this.updatedAt = createdAt;
    }

    public static ReadStatus of(UUID userId, UUID channelId) {
        return new ReadStatus(userId, channelId);
    }

    public ReadStatus update() {
        this.updatedAt = Instant.now();
        return this;
    }

    @Override
    public String toString() {
        return "[ReadStatus] {id=" + id + ", userId=" + userId + ", channelId=" + channelId +
                ", \n\tcreatedAt=" + createdAt + ", updatedAt=" + updatedAt +  "'}" ;
    }
}
