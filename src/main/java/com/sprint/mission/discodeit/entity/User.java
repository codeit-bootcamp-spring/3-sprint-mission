package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;

@Getter
public class User implements Serializable {
    private static final long serialVersionUID = 5020789657574048869L;
    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;
    //
    private String username;
    private String email;
    private String password;
    private UUID portraitId;

    public User(String username, String email, String password) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.ofEpochSecond(Instant.now().getEpochSecond());
        //
        this.username = username;
        this.email = email;
        this.password = password;
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
            this.updatedAt = Instant.ofEpochSecond(Instant.now().getEpochSecond());
        }
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
}

