package com.sprint.mission.discodeit.dto.entity;

import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class User implements Serializable {
    private final UUID id;
    private String name;
    private String email;
    private String password;
    private final Instant createdAt;
    private Instant updatedAt;
    private UUID userStatusId;
    private UUID profileImageId;

    @Serial
    private static final long serialVersionUID = 1788974919885878812L;

    public User(String username, String email, String password) {
        this.id = UUID.randomUUID();
        this.email = email;
        this.name = username;
        this.password = password;
        this.createdAt = Instant.now();
        this.updatedAt = createdAt;
    }

    public static User of(UserCreateRequest userCreateRequest) {
        return new User(userCreateRequest.getName(), userCreateRequest.getEmail(), userCreateRequest.getPassword());
    }

    public User updateUserStatusId(UUID userStatusId) {
        this.userStatusId = userStatusId;
        return this;
    }

    public User updateProfileImageId(UUID profileImageId) {
        this.profileImageId = profileImageId;
        return this;
    }

    @Override
    public String toString() {
        return "[User] {" + name + ": id=" + id + ", email=" + email + ", password=" + password + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + "}";
    }
}