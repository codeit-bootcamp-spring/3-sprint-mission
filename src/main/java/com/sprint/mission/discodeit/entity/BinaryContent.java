package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;

public class BinaryContent implements Serializable {
    private static final long serialVersionUID = 1L;

    @Getter
    private UUID id;

    @Getter
    private byte[] data;

    @Getter
    private Instant createdAt;

    public BinaryContent(byte[] data) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        //
        this.data = data;
    }
}
