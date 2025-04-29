package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;

@Getter
public class BinaryContent implements Serializable {

    @Serial
    private static final long serialVersionUID = 1887370024504996215L;

    private final UUID id;
    private final Instant createdAt;

    private final byte[] data;
    private final String contentType;
    private final String fileName;

    public BinaryContent(byte[] data, String contentType, String fileName) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.data = data;
        this.contentType = contentType;
        this.fileName = fileName;
    }

    @Override
    public String toString() {
        return "BinaryContent{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", data=" + Arrays.toString(data) +
                ", contentType='" + contentType + '\'' +
                ", fileName='" + fileName + '\'' +
                '}';
    }
}
