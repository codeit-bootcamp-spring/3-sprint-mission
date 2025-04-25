package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Channel implements Serializable {
    @Getter
    private static final Long serialVersionUID = 1L;
    //
    @Getter
    private final UUID id;
    @Getter
    private final Long createdAt;
    @Getter
    private Long updatedAt;
    //
    @Getter
    private String name;
    @Getter
    private ChannelType type;
    @Getter
    private String description;
    @Getter
    private UUID ownerId;
    @Getter
    private List<UUID> attendees;
    @Getter
    private List<UUID> messages;

    public Channel(String name, ChannelType type, String description, UUID ownerId) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now().getEpochSecond();
        this.updatedAt = Instant.now().getEpochSecond();
        //
        this.name = name;
        this.type = type;
        this.description = description;
        this.ownerId = ownerId;
        //
        this.attendees = new ArrayList<>();
        this.messages = new ArrayList<>();
    }

    public void addAttendee(UUID userId) {
        this.attendees.add(userId);
    }

    public void removeAttendee(UUID userId) {
        this.attendees.remove(userId);
    }

    public void addMessage(UUID messageId) {
        this.messages.add(messageId);
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
            this.updatedAt = Instant.now().getEpochSecond();
        }
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.systemDefault());

        String createdAtFormatted = formatter.format(Instant.ofEpochSecond(createdAt));
        String updatedAtFormatted = formatter.format(Instant.ofEpochSecond(updatedAt));

        return "📦 Channel {\n" +
                "  id         = " + id + "\n" +
                "  createdAt  = " + createdAtFormatted + "\n" +
                "  updatedAt  = " + updatedAtFormatted + "\n" +
                "  name       = '" + name + "'\n" +
                "  attendees  = " + attendees + "\n" +
                "  messages   = " + messages + "\n" +
                "}";
    }
}
