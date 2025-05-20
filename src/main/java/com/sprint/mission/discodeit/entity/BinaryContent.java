package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class BinaryContent implements Serializable {
    private static final long serialVersionUID = -4189267704562128762L;
    private UUID id;
    private Instant createdAt;
    //
    public BinaryContent(String fileName, Long size, String contentType, byte[] content) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.fileName = fileName;
        this.size = size;
        this.contentType = contentType;
        this.content = content;
    }

    private String fileName;
    private Long size;
    private String contentType;
    private byte[] content;
}
