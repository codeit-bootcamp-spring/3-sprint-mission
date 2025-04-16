package com.sprint.mission.discodeit.entitiy;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public class Channel implements Serializable {

    private static final long serialVersionUID = 1L;

    private UUID id;
    private Long createdAt;
    private Long updatedAt;
    private String channelName;
    private List<User> members;

    @Override
    public String toString() {
        return "Channel{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", channelName='" + channelName + '\'' +
                ", members=" + members +
                '}';
    }

    public Channel(String channelName, List<User> members) {
        this.channelName = channelName;
        this.members = members;
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
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

    public String getChannelName() {
        return channelName;
    }

    public List<User> getMembers() {
        return members;
    }

    public void updateId(UUID id) {
        this.id = id;
    }

    public void updateCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public void updateUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void updateChannelName(String channelName) {
        this.channelName = channelName;
    }

    public void updateMembers(List<User> members) {
        this.members = members;
    }
}
