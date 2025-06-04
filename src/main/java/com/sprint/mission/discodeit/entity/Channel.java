package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.*;

@Getter
public class Channel implements Serializable {
    private final UUID id;
    private ChannelType type;
    private String name;
    private String description;

    private final Instant createdAt;
    private Instant updatedAt;

    @Serial
    private static final long serialVersionUID = 5544881428014541716L;

    private Channel(ChannelType type, String name, String description) {
        this.id = UUID.randomUUID();
        this.type = type;
        this.name = name;
        this.description = description;
        this.createdAt = Instant.now();;
        this.updatedAt = this.createdAt;
    }

    public static Channel of(ChannelType type, String name, String description) {
        return new Channel(type, name, description);
    }

    public void update(String newName, String newDescription) {
        boolean anyValueUpdated = false;
        if (newName != null && !newName.equals(this.name)) {
            this.name = newName;
            anyValueUpdated = true;
        }
        if (newDescription != null && !newDescription.equals(this.description)) {
            this.description = newDescription;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.updatedAt = Instant.now();
        }
    }

    @Override
    public String toString() {
        return "[Channel] {" + type + " name=" + name + " id=" +  id + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + "}";
    }
}
