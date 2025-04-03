package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Channel {
    private String channelName;
    private UUID id;
    private Long createdAt;
    private Long updateAt;

    public Channel(String channelName) {
        this.channelName = channelName;
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updateAt = System.currentTimeMillis();
    }

    public void updateChannel(String channelName) {
        this.channelName = channelName;
        this.updateAt = System.currentTimeMillis();
    }

    public String getChannelName() {
        return channelName;
    }

    public UUID getId() {
        return id;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdateAt() {
        return updateAt;
    }
}
