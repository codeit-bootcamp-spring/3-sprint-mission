package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class BinaryContent implements Serializable {
    private static final long serialVersionUID = 1L;
    private final UUID id;
    private final Instant createdAt;
    private String fileName;
    private String mimeType;
    private byte[] data;

    public BinaryContent() {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
    }

    public void updateFileName(String fileName) {
        this.fileName = fileName;
    }

    public void updateMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public void updateData(byte[] data) {
        this.data = data;
    }
}
