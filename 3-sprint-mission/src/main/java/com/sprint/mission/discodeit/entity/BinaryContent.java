package com.sprint.mission.discodeit.entity;

import lombok.Data;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@Data
@Getter
@ToString
public class BinaryContent {
    private final UUID id;
    private final UUID userId;
    private String fileName;
    private byte[] content;
    private String contentType;
    private final Instant createdAt;

    public BinaryContent(UUID userId, String fileName, byte[] content, String contentType) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.fileName = fileName;
        this.content = content;
        this.contentType = contentType;
        this.createdAt = Instant.now();
    }



}
