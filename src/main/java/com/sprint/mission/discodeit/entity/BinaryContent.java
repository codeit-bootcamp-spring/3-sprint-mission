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
    private String path;
    private final Instant createdAt;

    @Serial
    private static final long serialVersionUID = -7687297973903907685L;

    private BinaryContent(String name, String path) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.path = path;
        this.createdAt = Instant.now();
    }

    public static BinaryContent of (String name, String path) {
        return new BinaryContent(name, path);
    }

    @Override
    public String toString() {
        return "[BinaryContent] {" + "id=" + id + ", fileName=" + name + ", filePath=" + path + ", createdAt=" + createdAt + '}';
    }
}