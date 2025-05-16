package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Channel implements java.io.Serializable{

    @Serial
    private static final long serialVersionUID = 1L;


    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;
    private String channelName;
    private UUID adminId;
    private ChannelType lock;
    private String password;
    private List<UUID> joiningUsers;

    public Channel() {
    }

    public Channel(String channelName, UUID adminId, ChannelType lock, String password) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.channelName = channelName;
        this.adminId = adminId;
        this.lock = lock;
        this.password = password;
        this.joiningUsers = new ArrayList<>();
    }

    public void updateId(UUID id) {
        this.id = id;
    }

    public void updateAdminId(UUID adminId) {
        this.adminId = adminId;
    }

    public void updateCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void updateUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void updateChannelName(String name) {
        this.channelName = name;
    }

    public void updateIsLock(ChannelType lock) {
        this.lock = lock;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.of("Asia/Seoul"));

        return "Channel{" +
                "id='" + id + '\'' +
                ", createdAt='" + formatter.format(createdAt) + '\'' +
                ", updatedAt='" + formatter.format(updatedAt) + '\'' +
                ", channelName=" + channelName + '\'' +
                ", lockState=" + lock +
                '}';
    }
}
