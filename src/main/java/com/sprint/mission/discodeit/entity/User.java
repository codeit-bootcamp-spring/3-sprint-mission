package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.UUID;

public class User implements Serializable {
    // uuid 타입 : 중복되지 않는 ID를 만드는 자바 내장 클래스
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
