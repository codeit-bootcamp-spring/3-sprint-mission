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


    // QUESTION. 처음에 생성될때 읽지않은 상태가 초기값이 맞나?
    public ReadStatus(UUID userId, UUID channelId) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.isRead = false;
        this.lastReadTime = null;

        //
        this.userId = userId;
        this.channelId = channelId;
    }

    public void update(Boolean isRead) {
        boolean anyValueUpdated = false;
        if (isRead != null && isRead != this.isRead) {
            this.isRead = isRead;
            this.lastReadTime = Instant.now();
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.updatedAt = Instant.now();

        }
    }
}
