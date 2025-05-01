package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.service.jcf.JCFUserService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.Serializable;
import java.sql.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Channel implements Serializable {
    private static final long serialVersionUID = 1L;
    private final UUID id;
    private String name;
    private final UUID maker;
    private List<UUID> entry;
    private final Long createdAt;
    private Long updatedAt;

    public Channel(UUID currentUser, String name) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.maker = currentUser;
        this.entry = new ArrayList<>();
        this.entry.add(currentUser);
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public UUID getMaker() {
        return maker;
    }

    public List<UUID> getEntry() {
        return entry;
    }

    // Date 타입 포매팅
    public String getCreatedAt() {
        String formattedCreatedTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(createdAt);
        return formattedCreatedTime;
    }

    public String getUpdatedAt() {
        String formattedUpdatedTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(updatedAt);
        return formattedUpdatedTime;
    }

    @Override
    public String toString() {
        return "Channel{" +
//                "id='" + id + '\'' +
                "name='" + name + '\'' +
//                ", maker='" + getMaker() + '\'' +
                // 포매팅된 date 사용
                ", createdAt=" + getCreatedAt() +
                ", updatedAt=" + getUpdatedAt() +
                '}';
    }

    public void addEntry(UUID userId) {
        if (entry == null) {
            this.entry.add(maker);
        } else {
            this.entry.add(userId);
        }
    }

    public void updateById(UUID id, String name) {
        this.name = name;
        updateDateTime();
    }

    public void updateDateTime() {
        this.updatedAt = System.currentTimeMillis();
    }
}
