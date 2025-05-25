package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class ReadStatus implements Serializable {
    private static final long serialVersionUID = 1L;
    private UUID id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    //
    private UUID userId;
    private UUID channelId;
    private LocalDateTime lastReadAt;

    public ReadStatus(UUID userId, UUID channelId, LocalDateTime lastReadAt) {
        this.id = UUID.randomUUID();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
        //
        this.userId = userId;
        this.channelId = channelId;
        this.lastReadAt = lastReadAt;
    }

    public void update(LocalDateTime newLastReadAt) {
        boolean anyValueUpdated = false;
        if (newLastReadAt != null && !newLastReadAt.equals(this.lastReadAt)) {
            this.lastReadAt = newLastReadAt;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.updatedAt = LocalDateTime.now();
        }
    }
}
