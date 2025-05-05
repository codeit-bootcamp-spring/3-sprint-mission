package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.UUID;

public class UserStatus implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Status {
        ONLINE,
        OFFLINE,
        AWAY,
        DO_NOT_DISTURB
    }

    private final UUID id;           // 고유 ID
    private final UUID userId;       // 상태를 가진 유저 ID
    private Status status;           // 현재 상태
    private final long createdAt;    // 상태 설정 시각
    private long updatedAt;          // 마지막 수정 시각

    public UserStatus(UUID userId, Status status) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.status = status;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status newStatus) {
        this.status = newStatus;
        this.updatedAt = System.currentTimeMillis();
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public String toString() {
        return "UserStatus{" +
                "id=" + id +
                ", userId=" + userId +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}