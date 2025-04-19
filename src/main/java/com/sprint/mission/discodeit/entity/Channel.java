package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Channel implements Serializable {

    private final UUID channelId;
    private String channelName;
    private boolean isPrivate;
    private String password;
    private final UUID ownerChannelId;
    private final long createdAt;
    private long updatedAt;
    private final Set<UUID> participants; // 참가자 목록
    private static final long serialVersionUID = 1L;

    // 생성자
    public Channel(String channelName, boolean isPrivate, String password, UUID ownerChannelId) {
        this.channelId = UUID.randomUUID();
        this.ownerChannelId = ownerChannelId;
        this.channelName = channelName;
        this.isPrivate = isPrivate;
        this.password = password;
        this.participants = new HashSet<>();
        this.participants.add(ownerChannelId);
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
    }

    // getter
    public UUID getChannelId() {
        return channelId;
    }

    public UUID getOwnerChannelId() {
        return ownerChannelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public String getPassword() {
        return password;
    }

    public boolean addParticipant(UUID userId) {
        return participants.add(userId);
    }

    public boolean removeParticipant(UUID userId) {
        return participants.remove(userId);
    }

    public Set<UUID> getParticipants() {
        return new HashSet<>(participants); // 불변성을 위해 복사본 반환
    }

    public boolean isParticipant(UUID userId) {
        return participants.contains(userId);
    }

    public void updateChannelName(String channelName) {
        this.channelName = channelName;
        this.updatedAt = System.currentTimeMillis();
    }

    public void updatePrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
        this.updatedAt = System.currentTimeMillis();
    }

    public void updatePassword(String password) {
        this.password = password;
        this.updatedAt = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "Channel{"
                + "channelId=" + channelId
                + ", channelName='" + channelName + '\''
                + ", isPrivate=" + isPrivate
                + ", password='" + password + '\''
                + ", createdAt=" + createdAt
                + ", updatedAt=" + updatedAt
                + ", ownerChannelId=" + ownerChannelId
                + '}';
    }
}
