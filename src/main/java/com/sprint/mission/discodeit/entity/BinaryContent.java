package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;

@Getter
public class BinaryContent implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private Instant createdAt;
    //
    private String fileName;
    private Long size;
    private String contentType;
    private byte[] bytes;

    public BinaryContent(String fileName, Long size, String contentType, byte[] bytes) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        //
        this.fileName = Objects.requireNonNull(fileName, "File name must not be null");
        this.size = Objects.requireNonNull(size, "File size must not be null");
        this.contentType = Objects.requireNonNull(contentType, "Content type must not be null");
        this.bytes = Objects.requireNonNull(bytes, "Binary data must not be null");
    }
}
