package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class BinaryContent implements Serializable {
    @Serial
    private static final long serialVersionUID = -4189267704562128762L;
    private final UUID id;
    private final Instant createdAt;
    //
    public BinaryContent(String fileName, Long size, String contentType, byte[] content) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.fileName = fileName;
        this.size = size;
        this.contentType = contentType;
        this.content = content;
    }

    private final String fileName;
    private final Long size;
    private final String contentType;
    private final byte[] content;
}
