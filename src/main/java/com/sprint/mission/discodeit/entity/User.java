package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class User {

    // 공통 필드: 객체 id, 객체 생성시간, 객체 수정시간
    // 선택 필드: 사용자 이름, 성별,
    private final UUID id;
    private final Long createdAt;
    private Long updatedAt;
    private String username;

    public User(String username) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = createdAt;
        this.username = username;
    }

    public void update(String name) {
        this.username = name;
        this.updatedAt = System.currentTimeMillis();
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

    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return "[User] {" + username + ": id=" + id + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + "}";
    }
}
