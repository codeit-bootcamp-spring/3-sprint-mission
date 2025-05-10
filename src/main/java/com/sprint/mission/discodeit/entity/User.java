package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class User implements Serializable {
    private static final long serialVersionUID = -7340272091383763402L;

    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;
    private String username;
    private String email;
    private String password;
    private boolean hasProfileImage;
    private UUID profileId;


    public User(String username, String email, String password, boolean content) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.username = username;
        this.email = email;
        this.password = password;
        this.hasProfileImage = content;
        this.profileId = null;
    }

    public void setProfileId(UUID profileId) {
        this.profileId = profileId;
        this.hasProfileImage = true;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    public void update(String newUsername, String newEmail, String newPassword) {
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

        if (anyValueUpdated) {
            this.updatedAt = Instant.now();
        }
    }
}
