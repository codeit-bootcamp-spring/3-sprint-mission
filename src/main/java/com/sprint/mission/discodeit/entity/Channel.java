package com.sprint.mission.discodeit.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Channel {
    private final UUID id;
    private final Long createdAt;
    private Long updatedAt;
    //
    private String name;
    private ChannelType type;
    private String description;
    private UUID ownerId;
    private List<UUID> attendees;
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

    public UUID getId() {
        return id;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public String getName() {
        return name;
    }

    public ChannelType getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public List<UUID> getAttendees() {
        return attendees;
    }

    public void addAttendee(UUID userId) {
        this.attendees.add(userId);
    }

    public List<UUID> getMessages() {
        return messages;
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
        return "Channel{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", name='" + name + '\'' +
                ", attendees=" + attendees +
                ", messages=" + messages +
                '}';
    }
}
