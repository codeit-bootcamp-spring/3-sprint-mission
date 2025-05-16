package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Getter
public class UserStatus implements Serializable {

    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;
    private UUID userId;
    private OnlineStatus status;

    public UserStatus() {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.status = OnlineStatus.ONLINE;
    }

    public UserStatus(UUID userId) {
        this.id = UUID.randomUUID();;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.userId = userId;
        this.status = OnlineStatus.ONLINE;
    }

    public void updateId(UUID id) {
        this.id = id;
    }

    public void updateCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void updateUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void updateUserId(UUID userId) {
        this.userId = userId;
    }

    public void updateStatus(OnlineStatus status) {
        this.status = status;
    }

    public Boolean isOnline() {
        Instant instantFiveMinutesAgo = Instant.now().minus(Duration.ofMinutes(5));

        return updatedAt.isAfter(instantFiveMinutesAgo);
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.of("Asia/Seoul"));

        return "UserStatus{" +
                "id=" + id +
                ", createdAt=" + formatter.format(createdAt) +
                ", updatedAt=" + formatter.format(updatedAt) +
                ", userId=" + userId +
                ", status=" + status +
                '}';
    }
}
