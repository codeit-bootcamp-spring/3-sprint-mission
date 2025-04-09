package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.*;

public class Channel {
    private final String id;
    private String name;
    private final String maker;
    private final Long createdAt;
    private Long updatedAt;

    public Channel(String name, User user) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.maker = user.getName();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }

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
                ", maker='" + maker + '\'' +
                // 포매팅된 date 사용
                ", createdAt=" + getCreatedAt() +
                ", updatedAt=" + getUpdatedAt() +
                '}';
    }

    public void updateById(String id, String name) {this.name = name;}
    public void updateDateTime() {this.updatedAt = System.currentTimeMillis();}

}
