package com.sprint.mission.discodeit.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@ToString
public class BinaryContent implements Serializable {
    private final UUID id;
    private String fileName;
    private Long size;
    private String contentType;
    private byte[] content;
    private final Instant createdAt;

    @Builder
    public BinaryContent(String fileName, Long size, String contentType, byte[] content) {
        this.id = UUID.randomUUID();
        this.fileName = fileName;
        this.size = size;
        this.contentType = contentType;
        this.content = content;
        this.createdAt = Instant.now();
    }



}
