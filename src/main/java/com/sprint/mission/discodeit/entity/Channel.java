package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.UUID;
import java.time.Instant;

import lombok.Getter;

@Getter
public class Channel implements Serializable {

    private final UUID channelId;
    private String channelName;
    private String password;
    private final UUID ownerChannelId;
    private final ChannelType channelType;
    private final Instant createdAt;
    private Instant updatedAt;
  
    private static final long serialVersionUID = 1L;

    // 생성자
    public Channel(ChannelType channelType, String channelName, String password, UUID ownerChannelId) {
        this.channelId = UUID.randomUUID();
        this.channelType = channelType;
        this.ownerChannelId = ownerChannelId;
        this.channelName = channelName;
        this.password = password;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public void updateChannelName(String channelName) {
        this.channelName = channelName;
        this.updatedAt = Instant.now();
    }

    public void updatePassword(String password) {
        this.password = password;
        this.updatedAt = Instant.now();
    }

    @Override
    public String toString() {
        return "Channel{"
                + "channelId=" + channelId
                + ", channelName='" + channelName + '\''
                + ", password='" + password + '\''
                + ", createdAt=" + createdAt
                + ", updatedAt=" + updatedAt
                + ", ownerChannelId=" + ownerChannelId
                + '}';
    }
}
