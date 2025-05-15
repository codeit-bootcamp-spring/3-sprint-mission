package com.sprint.mission.discodeit.entitiy;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private UUID id;
    private UUID profileId;
    private Instant createdAt;
    private Instant updatedAt;
    private String username;
    private String password;
    private String email;
    private List<UUID> friends;

    public User(UUID profileId, String username, String password, String email) {
        this.profileId = profileId;
        this.username = username;
        this.password = password;
        this.email = email;
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", profileId=" + profileId +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
