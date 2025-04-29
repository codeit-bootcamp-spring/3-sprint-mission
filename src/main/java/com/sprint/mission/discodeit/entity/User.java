package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Getter
public class User implements Serializable {
    private static final long serialVersionUID = -1421022282607757997L;

    private final UUID id;
    private final Long createdAt;
    private Long updatedAt;
    private String userName;

    public User(String userName) {
        this.id = UUID.randomUUID();
        this.createdAt = new Date().getTime();
        this.updatedAt = createdAt;
        this.userName = userName;
    }

    public void updateTime() {
        this.updatedAt = System.currentTimeMillis();
    }

    public void updateUserName(String userName) {
        this.userName = userName;
        updateTime();
    }

    @Override
    public String toString() {
        return "User{" +
                "userName='" + userName + '\'' +
                ", createdAt=" + new Date(getCreatedAt()) +
                ", updatedAt=" + new Date(getUpdatedAt()) +
                '}';
    }
}
