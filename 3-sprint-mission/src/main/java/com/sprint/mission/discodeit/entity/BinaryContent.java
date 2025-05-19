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
//    private final UUID userId;
    private String fileName;
    private Long size;
    private String contentType;
    private byte[] content;
    private final Instant createdAt;

    @Builder
    public BinaryContent(String fileName, String contentType, byte[] content) {
        this.id = UUID.randomUUID();
//        this.userId = userId;
        this.fileName = fileName;
        this.size = (long) content.length;
        this.contentType = contentType;
        this.content = content;
        this.createdAt = Instant.now();
    }



}
