package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import java.time.Instant;
import java.util.Objects;
import lombok.Getter;

@Getter
public class User extends BaseUpdatableEntity {
    private String username;
    private String email;
    private String password;
    private BinaryContent profile;
    private UserStatus status;

    public User(String username, String email, String password, BinaryContent profile) {
        this.username = Objects.requireNonNull(username, "Username must not be null");
        this.email = Objects.requireNonNull(email, "Email must not be null");
        this.password = Objects.requireNonNull(password, "Password must not be null");
        this.profile = profile;
    }

    public void update(String newUsername, String newEmail, String newPassword, BinaryContent newProfile) {
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
        if (newProfile != null && !newProfile.equals(this.profile)) {
            this.profile = newProfile;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.updatedAt = Instant.now();
        }
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + getUsername() + '\'' +
                ", id='" + getId() + '\'' +
                ", createdAt='" + getCreatedAt() + '\'' +
                ", updatedAt='" + getUpdatedAt() + '\'' +
                '}';
    }
}
