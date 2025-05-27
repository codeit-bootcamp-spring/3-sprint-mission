package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class Channel implements Serializable {
    @Serial
    private static final long serialVersionUID = -7716860811813642738L;
    private final UUID id;
    private final Instant createdAt;
    private Instant updatedAt;
    //
    private final ChannelType type;
    private String name;
    private String description;

    public Channel(ChannelType type, String name, String description) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        //
        this.type = type;
        this.name = name;
        this.description = description;
    }

    public void update(String newName, String newDescription) {
        boolean isupdated = false;
        if (newName != null && !newName.equals(this.name)) {
            this.name = newName;
            isupdated = true;
        }
        if (newDescription != null && !newDescription.equals(this.description)) {
            this.description = newDescription;
            isupdated = true;
        }
        if (isupdated) {
            this.updatedAt = Instant.now();
        }
    }

    @Override
    public String toString() {
        return "Channel{" +
                "id=" + id +
                ", type=" + type +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
