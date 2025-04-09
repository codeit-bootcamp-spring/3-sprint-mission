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

    // Date 타입 포매팅
    public String getCreatedAt() {
        String formattedCreatedTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(createdAt);
        return formattedCreatedTime;}
    public String getUpdatedAt() {
        String formattedUpdatedTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(updatedAt);
        return formattedUpdatedTime;}

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                // 포매팅된 date 사용
                ", createdAt=" + getCreatedAt() +
                ", updatedAt=" + getUpdatedAt() +
                '}';
    }

    public void updateById(String id, String name) {this.name = name;}
    public void updateDateTime() {this.updatedAt = System.currentTimeMillis();}
}

