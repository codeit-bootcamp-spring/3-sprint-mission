package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class BinaryContent {
    private UUID id;
    private Instant createdAt;

    private String fileName;
    private BinaryContentType type;
    private Long filesize;
    private byte[] bytes;

    public BinaryContent(String fileName, BinaryContentType type, Long filesize, byte[] bytes) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();

        this.fileName = fileName;
        this.type = type;
        this.filesize = filesize;
        this.bytes = bytes;
    }





}
