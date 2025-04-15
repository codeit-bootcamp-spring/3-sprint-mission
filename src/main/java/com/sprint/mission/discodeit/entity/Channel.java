package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.entity
 * fileName       : Channel
 * author         : doungukkim
 * date           : 2025. 4. 3.
 * description    : channel entity
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 3.        doungukkim       최초 생성
 */
public class Channel {
    private UUID id;
    private Long createdAt;
    private Long updatedAt;
    private String title;
    private List<Message> messages;
    private List<UUID> usersIds;

    public Channel(UUID userId) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.messages = new ArrayList<>();
        this.usersIds =new ArrayList<>();
        this.usersIds.add(userId);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public List<UUID> getUsersIds() {
        return usersIds;
    }

    public void setUsersIds(List<UUID> usersIds) {
        this.usersIds = usersIds;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Channel{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", title='" + title + '\'' +
                ", messages=" + messages +
                ", usersIds=" + usersIds +
                '}';
    }
}
