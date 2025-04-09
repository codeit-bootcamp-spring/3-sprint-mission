package com.sprint.mission.discodeit.entity;

import java.util.*;

public class Channel extends BaseEntity {
    private String channelName;
    private final Set<UUID> userIds = new HashSet<>();
    private final List<UUID> messageIds = new ArrayList<>();

    public Channel(String channelName) {
        this.channelName = channelName;
    }

    public String getChannelName() {
        return channelName;
    }

    public void updateChannelName(String channelName) {
        this.channelName = channelName;
        updateTime();
    }

    public Set<UUID> getUserIds() {
        return userIds;
    }

    public List<UUID> getMessageIds() { return messageIds; }

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
        return "Channel{" + "channelName=" + channelName +
                ", userIds=" + userIds +
                ", messageIds=" + messageIds +
                ", createdAt=" + getCreatedAt() +
                ", updatedAt=" + getUpdatedAt() + '}';
    }
}
