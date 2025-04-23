package com.sprint.mission.discodeit.entitiy;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
public class Channel implements Serializable {

    private static final long serialVersionUID = 1L;

    private UUID id;
    private final Instant createdAt;
    private Instant updatedAt;
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
        this.createdAt =Instant.now();
    }


    public void updateId(UUID id) {
        this.id = id;
    }

    public void updateUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void updateChannelName(String channelName) {
        this.channelName = channelName;
    }

    public void updateMembers(List<User> members) {
        this.members = members;
    }
}
