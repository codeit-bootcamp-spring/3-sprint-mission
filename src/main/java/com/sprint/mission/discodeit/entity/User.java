package com.sprint.mission.discodeit.entity;

import java.time.Instant;
import java.util.UUID;

public class User {
    private UUID id;
    private long createdAt;
    private long updatedAt;

    private String name;
    private int age;


    public User(String name, int age) {
        this.name = name;
        this.age = age;
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now().getEpochSecond();
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

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    // 필드를 수정하는 update 함수를 정의하세요.
    public void update() {
        // TODO: need to update this.updatedAt
    }
}
