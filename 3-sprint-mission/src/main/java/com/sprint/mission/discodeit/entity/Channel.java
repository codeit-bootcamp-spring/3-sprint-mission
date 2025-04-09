package com.sprint.mission.discodeit.entity;

import java.util.List;
import java.util.UUID;

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

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public String toString() {
        return "Channel{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", maker='" + maker + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    public void updateById(String id, String name) {
        this.name = name;
    }

    public void updateDateTime() {this.updatedAt = System.currentTimeMillis();}

}
