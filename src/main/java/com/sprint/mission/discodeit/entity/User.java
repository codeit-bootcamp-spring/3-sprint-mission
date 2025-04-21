package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.UUID;

public class User implements Serializable {

    private final UUID id;
    private final long createdAt;
    private long updatedAt;
    private String username;

    public User(String username) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
        this.username = username;
    }

    public UUID getId() { return id; }
    public long getCreatedAt() { return createdAt; }
    public long getUpdatedAt() { return updatedAt; }
    public String getUsername() { return username; }

    public void updateUsername(String Name) {
        this.username = Name;
        this.updatedAt = System.currentTimeMillis();
    }
}
