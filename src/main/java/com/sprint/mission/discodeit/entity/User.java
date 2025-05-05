package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

public class User implements Serializable { // 직렬화를 위해 Serializable 구현
    private static final long serialVersionUID = 1L;

    private final UUID id;               // 고유 사용자 ID
    private final Instant createdAt;     // 생성 시간
    private Instant updatedAt;           // 마지막 수정 시간
    private String name;                 // 사용자 이름

    // 생성자
    public User(String name) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
        this.name = name;
    }

    // 이름 및 시간 업데이트
    public void updateName(String name) {
        this.name = name;
        this.updatedAt = Instant.now();
    }

    public void setName(String newName) {
        this.name = newName;
    }

    public void updateUpdatedAt() {
        this.updatedAt = Instant.now();
    }

    // Getter
    public UUID getId() {
        return id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}