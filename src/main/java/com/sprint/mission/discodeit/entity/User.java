package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private UUID id;
    private long createdAt;
    private long updatedAt;

    private String name;
    private int age;


    public User(String name, int age) {
        this.name = name;
        this.age = age;
        // for fixed unique id
        this.id = UUID.nameUUIDFromBytes(name.concat(String.valueOf(age)).getBytes());
        this.createdAt = Instant.now().getEpochSecond();
        this.updatedAt = Instant.now().getEpochSecond();

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
    public User update(String name) {
        this.updatedAt = Instant.now().getEpochSecond();

        //TODO : to check if values are different before update
        this.name = name;

        return this;
    }

    public User update(int age) {
        this.updatedAt = Instant.now().getEpochSecond();

        //TODO : to check if values are different before update
        this.age = age;

        return this;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
