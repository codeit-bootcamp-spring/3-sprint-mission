package com.sprint.mission.discodeit.entitiy;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private UUID id;
    private UUID profileId;
    private final Instant createdAt;
    private Instant updatedAt;
    private String userName;
    private String password;
    private String email;
    private Map<UUID,User> friends;

    public User(String userName, String password, String email, Map<UUID,User> friends) {
        this.userName = userName;
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
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", friends=" + friends +
                '}';
    }

    public void updateUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void updateUserName(String userName) {
        this.userName = userName;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateEmail(String email) {
        this.email = email;
    }

    public void updateFriends(Map<UUID,User> friends) {
        this.friends = friends;
    }
}
