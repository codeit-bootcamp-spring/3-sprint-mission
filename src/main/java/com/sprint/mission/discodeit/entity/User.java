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
    private String email;
    private String password;

    private UUID profileId;

    public User(String userName, String email, String password) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = createdAt;
        this.userName = userName;
        this.email = email;
        this.password = password;
    }

    public void updateTime() {
        this.updatedAt = Instant.now();
    }

    public void updateUser(String userName, String email, String password) {
        boolean anyValueUpdated = false;
        if (userName != null && !userName.equals(this.userName)) {
            this.userName = userName;
            anyValueUpdated = true;
        }
        if (email != null && !email.equals(this.email)) {
            this.email = email;
            anyValueUpdated = true;
        }
        if (password != null && !password.equals(this.password)) {
            this.password = password;
            anyValueUpdated = true;
        }
        if (anyValueUpdated) {
            updateTime();
        }

    }

    public void updateProfileId(UUID profileId) {
        this.profileId = profileId;
        updateTime();
    }

    @Override
    public String toString() {
        return "User{" +
                "userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", profileId=" + profileId +
                ", createdAt=" + Date.from(createdAt) +
                ", updatedAt=" + Date.from(updatedAt) +
                '}';
    }
}
