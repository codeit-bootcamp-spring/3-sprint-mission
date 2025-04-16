package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.UUID;

public class Channel implements Serializable {
    private static final long serialVersionUID = 1L;
    private static int counter = 1;

    private int number;
    private String channelName;
    private UUID id;
    private long createdAt;
    private long updateAt;

    public Channel(String channelName) {
        this.number = counter++;
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

    public int getChannelNumber() {
        return number;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdateAt() {
        return updateAt;
    }

    public static void setCounter(int newCounter) {
        counter = newCounter;
    }

    @Override
    public String toString() {
        return "Channel{" +
                "number=" + number +
                ", channelName='" + channelName + '\'' +
                ", id=" + id +
                ", createdAt=" + createdAt +
                ", updateAt=" + updateAt +
                '}';
    }
}