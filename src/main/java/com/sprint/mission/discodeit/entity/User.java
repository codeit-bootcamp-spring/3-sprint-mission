package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Getter
public class User implements Serializable {
    private final UUID id;
    private final Long createdAt;
    private Long updatedAt;
    private String name;

    @Serial
    private static final long serialVersionUID = 1L;

    public User(String username) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = createdAt;
        this.name = username;
    }

    public static User of(String username) {
        return new User(username);
    }

    public void setName(String name) {
        this.name = name;
        this.updatedAt = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "[User] {" + name + ": id=" + id + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + "}";
    }
}
