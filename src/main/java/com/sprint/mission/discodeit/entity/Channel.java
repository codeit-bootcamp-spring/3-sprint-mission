package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.*;

@Getter
public class Channel implements Serializable {

    @Serial
    private static final long serialVersionUID = 3253334103732539416L;

    private final UUID id;
    private final Instant createdAt;
    private Instant updatedAt;
    private String channelName;
    private String description;
    private ChannelType type;
    private final Set<UUID> userIds = new HashSet<>();
    private final List<UUID> messageIds = new ArrayList<>();

    public Channel(String channelName, String description, ChannelType type) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = createdAt;
        this.channelName = channelName;
        this.description = description;
        this.type = type;
    }

    public void updateTime() {
        this.updatedAt = Instant.now();
    }

    public void updateChannel(String channelName, String description) {
        boolean anyValueUpdated = false;
        if (channelName != null && !channelName.equals(this.channelName)) {
            this.channelName = channelName;
            anyValueUpdated = true;
        }
        if (description != null && !description.equals(this.description)) {
            this.description = description;
            anyValueUpdated = true;
        }
        if (anyValueUpdated) {
            updateTime();
        }
    }

    public void addUser(UUID userId) {
        userIds.add(userId);
        updateTime();
    }

    public void addMessage(UUID messageId) {
        messageIds.add(messageId);
        updateTime();
    }

    @Override
    public String toString() {
        return "Channel{" +
                "channelName='" + channelName + '\'' +
                ", type=" + type +
                ",description='" + description + '\'' +
                ", userIds=" + userIds +
                ", messageIds=" + messageIds +
                ", createdAt=" + Date.from(createdAt) +
                ", updatedAt=" + Date.from(updatedAt) +
                '}';
    }
}
