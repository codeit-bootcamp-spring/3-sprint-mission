package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.dto.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.PublicChannelCreateRequest;
import lombok.Getter;
import lombok.Setter;

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
    private final Instant createdAt;
    @Getter
    private Instant updatedAt;
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
    @Getter
    @Setter
    private Instant lastMessageTime;

    public Channel(PublicChannelCreateRequest publicChannelCreateRequest) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        //
        this.name = publicChannelCreateRequest.name();
        this.type = publicChannelCreateRequest.type();
        this.description = publicChannelCreateRequest.description();
        this.ownerId = publicChannelCreateRequest.ownerId();
        //
        this.attendees = List.of(this.ownerId);
        this.messages = new ArrayList<>();
    }

    public Channel(PrivateChannelCreateRequest privateChannelCreateRequest) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        //
        this.type = privateChannelCreateRequest.type();
        this.ownerId = privateChannelCreateRequest.ownerId();
        //
        this.attendees = List.of(this.ownerId);
        this.messages = new ArrayList<>();
    }

    //XXX : channel.addAttendees(), 이게 repository가 아니라 여기 있는게 맞을까?
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
                "  attendees  = " + attendees + "\n" +
                "  messages   = " + messages + "\n" +
                "}";
    }
}
