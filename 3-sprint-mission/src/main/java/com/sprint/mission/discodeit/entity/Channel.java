package com.sprint.mission.discodeit.entity;

import java.text.SimpleDateFormat;
import java.util.UUID;
import java.util.List;

public class Channel {
    private final UUID id;
    private int number;
    private String name;
    private final User maker;
    private List<User> users;
    private final Long createdAt;
    private Long updatedAt;

    public Channel(String name, User user) {
        this.id = UUID.randomUUID();
        this.number = 0;
        this.name = name;
        this.maker = user;
        this.users = null;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    public UUID getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public List<User> getUsers() {return users;}
    public int getNumber() {return number;}

    // Date 타입 포매팅
    public String getCreatedAt() {
        String formattedCreatedTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(createdAt);
        return formattedCreatedTime;}
    public String getUpdatedAt() {
        String formattedUpdatedTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(updatedAt);
        return formattedUpdatedTime;}

    @Override
    public String toString() {
        return "Channel{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", maker='" + maker.getName() + '\'' +
                // 포매팅된 date 사용
                ", createdAt=" + getCreatedAt() +
                ", updatedAt=" + getUpdatedAt() +
                '}';
    }

    public void addUsers(List<User> users) {this.users.addAll(users);}
    public void removeUsers(List<User> users) {this.users.removeAll(users);}
    public void updateById(UUID id, String name) {this.name = name;}
    public void updateDateTime() {this.updatedAt = System.currentTimeMillis();}

}
