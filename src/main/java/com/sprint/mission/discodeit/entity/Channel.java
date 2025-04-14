package com.sprint.mission.discodeit.entity;

import java.util.*;

public class Channel {
    private final UUID id;
    private final Long createdAt;
    private Long updatedAt;
    private String channelName;
    private Set<UUID> members;

    public Channel(String name) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
        this.channelName = name;
        this.members = new HashSet<UUID>();
    }

    public UUID getId() {
        return id;
    }

    public long getCreatedAt() {
        return createdAt;
    }
    public long getUpdatedAt() {
        return updatedAt;
    }

    public String getChannelName() {
        return channelName;
    }

    public Set<UUID> getMembers() {
        return members;
    }

    public void join(UUID userId) {
        members.add(userId);
    }

    public void leave(UUID userId) {
        members.remove(userId);
    }

    public boolean isMember(UUID userId) {
        return members.contains(userId);
    }

    public void setChannelName(String name) {
        this.channelName = name;
        this.updatedAt = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "[Channel] {" + channelName + " id=" +  id + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + "}"
                + "\n\tmembers=" + members + "}";
    }
}
