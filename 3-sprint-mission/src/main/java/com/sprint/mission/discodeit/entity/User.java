package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

@Getter
public class User implements Serializable {
    private final UUID id;
    private String username;
    private String email;
    private String password;
    private String name;
    private UUID profileId;
    private final Instant createdAt;
    private Instant updatedAt;
    private Instant loginTime;

    @Builder
    public User(String username, String email, String password, String name, UUID profileId) {
        this.id = UUID.randomUUID();
        this.username = username;
        this.email = email;
        this.password = password;
        this.name = name;
        this.profileId = profileId;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.loginTime = Instant.now();
    }

    @Override
    public String toString() {

        return "User{" +
//                "id=" + id +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", loginTime=" + loginTime +
                '}';
    }

    public void update(String newUsername, String newEmail, String newPassword, String newName, UUID newProfileId) {
        boolean anyValueUpdated = false;
        if (newUsername != null && !newUsername.equals(this.username)) {
            this.username = newUsername;
            anyValueUpdated = true;
        }
        if (newEmail != null && !newEmail.equals(this.email)) {
            this.email = newEmail;
            anyValueUpdated = true;
        }
        if (newPassword != null && !newPassword.equals(this.password)) {
            this.password = newPassword;
            anyValueUpdated = true;
        }
        if (newName != null && !newName.equals(this.name)) {
            this.name = newName;
            anyValueUpdated = true;
        }
        if (newProfileId != null && !newProfileId.equals(this.profileId)) {
            this.profileId = newProfileId;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.updatedAt = Instant.now();
        }
    }
}

