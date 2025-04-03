package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class User {
    private String username;
    private UUID id;
    private Long createdAt;
    private Long updatedAt;


    public User(String name) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
        this.username = name;

    }

    public void updateUserName(String username) {
        this.username = username;
        this.updatedAt = System.currentTimeMillis();
    }

    public String getUsername() {
        return username;
    }

    public UUID getId() {
        return id;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
