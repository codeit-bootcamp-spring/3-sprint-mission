package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.*;

@Getter
public class Channel implements Serializable {
    private static final long serialVersionUID = 1L;
    private final UUID id;
    private final Instant createdAt;
    private Instant updatedAt;
    private String channelName;
    private UUID channelMaster;
    private String description;
    private boolean isPrivate;
    private final List<UUID> users;
    private final List<UUID> messages;

    public Channel() {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.users = new ArrayList<>();
        this.messages = new ArrayList<>();
    }

    public void updateChannelName(String channelName) {
        this.channelName = channelName;
        this.updatedAt = Instant.now();
    }

    public void updateChannelMaster(UUID userId) {
        this.channelMaster = userId;
        this.updatedAt = Instant.now();
    }

    public void updateDescription(String description) {
        this.description = description;
        this.updatedAt = Instant.now();
    }

    public void updatePrivate(boolean aPrivate) {
        this.isPrivate = aPrivate;
    }

    public void updateUsers(UUID userId) {
        this.users.add(userId);
    }

    public void updateMessages(UUID messageId) {
        this.messages.add(messageId);
    }

    @Override
    public String toString() {
        return "Channel {\n" +
                "  id=" + id + ",\n" +
                "  createdAt=" + createdAt + ",\n" +
                "  updatedAt=" + updatedAt + ",\n" +
                "  channelName='" + channelName + "',\n" +
                "  channelMaster='" + channelMaster + "', \n" +
                "  description='" + description + "',\n" +
                "  users=" + users.stream().toList() + ",\n" +
                "  messages=" + messages.stream().toList() + "\n" +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Channel channel = (Channel) o;
        return Objects.equals(id, channel.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
