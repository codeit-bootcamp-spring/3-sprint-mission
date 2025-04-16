package com.sprint.mission.discodeit.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class User extends BaseEntity {
    private String userName;
    private final Set<UUID> channelIds = new HashSet<>();

    public User(String userName) {
        this.userName = userName;
    }

    public Set<UUID> getChannelIds() {
        return channelIds;
    }

    public void updateUserName(String userName) {
        this.userName = userName;
        updateTime();
    }

    public void addChannel(UUID channelId) {
        channelIds.add(channelId);
    }

    @Override
    public String toString() {
        return "User{name='" + userName + '\'' +
                ", channelIds=" + channelIds +
                ", createdAt=" + new Date(getCreatedAt()) +
                ", updatedAt=" + new Date(getUpdatedAt()) +
                '}';
    }
}
