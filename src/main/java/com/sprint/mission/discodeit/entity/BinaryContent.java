package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class BinaryContent implements Serializable {
    private final UUID id;
    private final String name;
    private Long size;
    private String contentType;
    private byte[] bytes;
    private final Instant createdAt;

    @Serial
    private static final long serialVersionUID = -7687297973903907685L;

    private BinaryContent(String name, Long size, String contentType, byte[] bytes) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.name = name;
        this.size = size;
        this.contentType = contentType;
        this.bytes = bytes;
    }

    public static BinaryContent of (String name, Long size, String contentType, byte[] bytes) {
        return new BinaryContent(name, size, contentType, bytes);
    }

    @Override
    public String toString() {
        return "[BinaryContent] {" + "id=" + id + ", fileName=" + name + ", size=" + size + ", contentType=" + contentType + ", createdAt= "+ createdAt + "}";
    }
}