package com.sprint.mission.discodeit.entity;

import java.time.Instant;
import java.util.UUID;
import lombok.Getter;

public class BinaryContent {
    private static final long serialVersionUID = 1L;

    @Getter
    private UUID id;

    @Getter
    private Instant createdAt;

    public BinaryContent(UUID id) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        //
    }
}
