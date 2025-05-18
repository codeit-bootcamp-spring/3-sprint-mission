package com.sprint.mission.discodeit.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@ToString
public class Channel implements Serializable {
    private final UUID id;
    private String name;
    private String description;
    private final UUID makerId;
    private final ChannelType type;
    private final Instant createdAt;
    private Instant updatedAt;
    private Instant lastMessageAt;

    @Builder
    public Channel(UUID makerId, String name, String description, ChannelType type) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.description = description;
        this.makerId = makerId;
        this.type = type;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.lastMessageAt = null;
    }

    public void update(String name, String descripton) {
        this.name = name;
        this.description = descripton;
        updateDateTime();
    }

    public void updateDateTime() {
        this.updatedAt = Instant.now();
    }

    public void updateMessageAt() {
        this.lastMessageAt = Instant.now();
    }
}
