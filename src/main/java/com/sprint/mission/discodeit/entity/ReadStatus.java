package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
@Getter
public class ReadStatus implements Serializable {
    private static final long serialVersionUID = -8598800178736921628L;
    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;
    //
    private UUID userId;
    private UUID channelId;
    private Instant recentReadAt;

    public void refresh(Instant newRecentReadAt) {
        this.recentReadAt = Instant.now();
    }
}
