package com.sprint.mission.discodeit.entitiy;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
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
    private Map<UUID,User> friends;

    public User(String username, String password, String email, Map<UUID,User> friends) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.friends = friends;
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", userName='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", friends=" + friends +
                '}';
    }
}
