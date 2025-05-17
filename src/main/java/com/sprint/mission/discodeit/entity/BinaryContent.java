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
    private final Instant createdAt;

    @Serial
    private static final long serialVersionUID = -7687297973903907685L;

    private BinaryContent(String name) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.createdAt = Instant.now();
    }

    public static BinaryContent of (String name) {
        return new BinaryContent(name);
    }

    @Override
    public String toString() {
        return "[BinaryContent] {" + "id=" + id + ", fileName=" + name + ", createdAt=" + createdAt + '}';
    }
}