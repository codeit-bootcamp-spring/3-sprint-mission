package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.UUID;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

@Getter
public class Channel implements Serializable {

    private final UUID channelId;
    private String channelName;
    private String password;
    private final UUID ownerId;
    private final ChannelType channelType;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<UUID> participantIds;
    private LocalDateTime lastMessageAt;

    private static final long serialVersionUID = 1L;

    // 생성자
    public Channel(ChannelType channelType, String channelName, String password, UUID ownerId) {
        this.channelId = UUID.randomUUID();
        this.channelType = channelType;
        this.ownerId = ownerId;
        this.channelName = channelName;
        this.password = password;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
        this.participantIds = new ArrayList<>();
        if (ownerId != null) {
            this.participantIds.add(ownerId);
        }
        this.lastMessageAt = null;
    }

    public void updateChannelName(String channelName) {
        this.channelName = channelName;
        this.updatedAt = LocalDateTime.now();
    }

    public void updatePassword(String password) {
        this.password = password;
        this.updatedAt = LocalDateTime.now();
    }

    public void setLastMessageAt(LocalDateTime lastMessageAt) {
        this.lastMessageAt = lastMessageAt;
        this.updatedAt = LocalDateTime.now();
    }

    public void addParticipant(UUID userId) {
        if (userId != null && !this.participantIds.contains(userId)) {
            this.participantIds.add(userId);
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void removeParticipant(UUID userId) {
        if (userId != null) {
            this.participantIds.remove(userId);
            this.updatedAt = LocalDateTime.now();
        }
    }

    @Override
    public String toString() {
        return "Channel{"
                + "channelId=" + channelId
                + ", channelName='" + channelName + '\''
                + ", password='" + password + '\''
                + ", createdAt=" + createdAt
                + ", updatedAt=" + updatedAt
                + ", ownerId=" + ownerId
                + ", channelType=" + channelType
                + ", participantIds=" + participantIds
                + ", lastMessageAt=" + lastMessageAt
                + '}';
    }
}
