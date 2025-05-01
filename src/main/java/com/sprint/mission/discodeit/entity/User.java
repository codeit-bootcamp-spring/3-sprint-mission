package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.UUID;

public class User implements Serializable {
    private static final long serialVersionUID = 2047441161413796353L;

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

    public void setNumber(int number) {
        this.number = number;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
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

    public static void setCounter(int newCounter) {
        counter = newCounter;
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