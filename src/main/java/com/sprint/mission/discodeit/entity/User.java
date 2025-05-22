package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.UUID;
import java.time.Instant;
import lombok.Getter;

@Getter
public class User implements Serializable {

    private final UUID userId;
    private String username;
    private String email;
    private String password;
    private final Instant createdAt;
    private Instant updatedAt;
    private static final long serialVersionUID = 1L;
    private UUID profileId;

    // 생성자
    public User(String username, String email, String password, UUID profileId) {
        this.userId = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.username = username;
        this.email = email;
        this.password = password;
        this.profileId = profileId;
    }

    public void update(String username, String email, String password, UUID profileId) {
        boolean updated = false;
        if (username != null && !username.isEmpty()) {
            this.username = username;
            updated = true;
        }
        if (email != null && !email.isEmpty()) {
            this.email = email;
            updated = true;
        }
        if (password != null && !password.isEmpty()) {
            this.password = password;
            updated = true;
        }
        if (profileId != null) {
            this.profileId = profileId;
            updated = true;
        }
        if (updated) {
            this.updatedAt = Instant.now();
        }
    }

    @Override
    public String toString() {
        return "User [userId=" + userId + ", username=" + username + ", email=" + email + ", createdAt=" + createdAt
                + ", updatedAt=" + updatedAt + ", password=" + password + "]";
    }
}
