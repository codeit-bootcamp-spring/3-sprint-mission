package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Getter
public class Channel implements Serializable {
    private static final Long serialVersionUID = 1L;
    //
    private final UUID id;
    private final Instant createdAt;
    private Instant updatedAt;
    //
    private String name;
    private ChannelType type;
    private String description;
    private UUID ownerId;
    @Setter
    private Instant lastMessageAt;

    public Channel(ChannelType type, UUID ownerId, String name, String description) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        //
        this.name = name;
        this.type = type;
        this.description = description;
        this.ownerId = ownerId;
        //
    }


    public void update(String name, String description) {
        boolean anyValueUpdated = false;
        if (name != null && !name.equals(this.name)) {
            this.name = name;
            anyValueUpdated = true;
        }
        if (description != null && !description.equals(this.description)) {
            this.description = description;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.updatedAt = Instant.now();
        }
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.systemDefault());

        String createdAtFormatted = formatter.format(createdAt);
        String updatedAtFormatted = formatter.format(updatedAt);

        return "📦 Channel {\n" +
                "  id         = " + id + "\n" +
                "  createdAt  = " + createdAtFormatted + "\n" +
                "  updatedAt  = " + updatedAtFormatted + "\n" +
                "  name       = '" + name + "'\n" +
                "  type       = '" + type + "'\n" +
                "  description = '" + description + "'\n" +
                "  ownerId     = '" + ownerId + "'\n" +
                "}";
    }
}
