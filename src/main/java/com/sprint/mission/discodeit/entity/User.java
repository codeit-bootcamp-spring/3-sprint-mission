package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Getter
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = -1421022282607757997L;

    private final UUID id;
    private final Instant createdAt;
    private Instant updatedAt;
    private String userName;

    private UUID profileId;

    public User(String userName) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = createdAt;
        this.userName = userName;
    }

    public void updateTime() {
        this.updatedAt = Instant.now();
    }

    public void updateUserName(String userName) {
        this.userName = userName;
        updateTime();
    }

    public void updateProfileId(UUID profileId) {
        this.profileId = profileId;
        updateTime();
    }

    @Override
    public String toString() {
        return "User{" +
                "userName='" + userName + '\'' +
                ", profileId=" + profileId +
                ", createdAt=" + Date.from(createdAt) +
                ", updatedAt=" + Date.from(updatedAt) +
                '}';
    }
}
