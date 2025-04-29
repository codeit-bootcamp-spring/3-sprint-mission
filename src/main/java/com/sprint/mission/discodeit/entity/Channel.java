package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.util.*;

@Getter
public class Channel implements Serializable {
    private static final long serialVersionUID = 3253334103732539416L;

    private final UUID id;
    private final Long createdAt;
    private Long updatedAt;
    private String channelName;
    private final Set<UUID> userIds = new HashSet<>();
    private final List<UUID> messageIds = new ArrayList<>();

    public Channel(String channelName) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = createdAt;
        this.channelName = channelName;
    }

    public void updateTime() {
        this.updatedAt = System.currentTimeMillis();
    }

    public void updateChannelName(String channelName) {
        this.channelName = channelName;
        updateTime();
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
                ", userIds=" + userIds +
                ", messageIds=" + messageIds +
                ", createdAt=" + new Date(getCreatedAt()) +
                ", updatedAt=" + new Date(getUpdatedAt()) +
                '}';
    }
}
