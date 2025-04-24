package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Getter
public class Message implements Serializable {
    private final UUID id;
    private final Long createdAt;
    private Long updatedAt;
    private final UUID userId;
    private final UUID channelId;
    private String content;

    @Serial
    private static final long serialVersionUID = 1L;

    private Message(UUID userId, UUID channelId, String content) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
        this.userId = userId;
        this.channelId = channelId;
        this.content = content;
    }

    public static Message of(UUID userId, UUID channelId, String content) {
        return new Message(userId, channelId, content);
    }

    public void update(String content) {
        this.content = content;
        this.updatedAt = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "[Message] {id=" + id + ", userId=" + userId + ", channelId=" + channelId +
                ", \n\tcontent='" + content + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt +  "'}" ;
    }
}
