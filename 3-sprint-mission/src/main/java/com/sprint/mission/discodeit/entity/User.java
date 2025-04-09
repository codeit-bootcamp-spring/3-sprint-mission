package com.sprint.mission.discodeit.entity;

import java.util.UUID;
import java.text.SimpleDateFormat;

public class User {
    private final String id;
//    private final String userid;
    private String name;
    private final Long createdAt;
    private Long updatedAt;

    public User(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    public String getId() {return id;}
    public String getName() {return name;}

    public Long getCreatedAt() {return createdAt;}
    public Long getUpdatedAt() {return updatedAt;}

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    public void updateById(String id, String name) {this.name = name;}
    public void updateDateTime() {this.updatedAt = System.currentTimeMillis();}
}

