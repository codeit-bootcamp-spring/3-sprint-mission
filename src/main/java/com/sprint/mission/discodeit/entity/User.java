package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class User {
    private static int counter = 1;
    private int number;
    private String username;
    private UUID id;
    private long createdAt;
    private long updatedAt;


    public User(String name) {
        this.number = counter++;
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

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public int getNumber() {
        return number;
    }

    @Override
    public String toString() {
        return "User{" +
                "number=" + number +
                ", username='" + username + '\'' +
                ", id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
