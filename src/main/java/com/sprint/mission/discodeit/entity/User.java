package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.entity
 * fileName       : User
 * author         : doungukkim
 * date           : 2025. 4. 3.
 * description    : user entity
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 3.        doungukkim       최초 생성
 */
public class User {
    private UUID id;
    private Long createdAt;
    private Long updatedAt;
    private String username;
    private List<UUID> channelIds;

    public User(String username) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.username = username;
        this.channelIds = new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }

    public List<UUID> getChannelIds() {
        return channelIds;
    }

    public void setChannelIds(List<UUID> channelIds) {
        this.channelIds = channelIds;
        this.updatedAt = System.currentTimeMillis();
    }

    public void setUsername(String username) {
        this.username = username;
        this.updatedAt = System.currentTimeMillis();
    }

    public UUID getId() {
        return id;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", username='" + username + '\'' +
                ", channelIds=" + channelIds +
                '}';
    }
}
