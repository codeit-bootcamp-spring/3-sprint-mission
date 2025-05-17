package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
public class Message implements Serializable {
    private final UUID id;
    private final UUID userId;
    private final UUID channelId;
    private String content;
    List<UUID> attachmentIds;
    private final Instant createdAt;
    private Instant updatedAt;

    @Serial
    private static final long serialVersionUID = 2087840481792494268L;

    private Message(UUID userId, UUID channelId, String content, List<UUID> attachmentIds) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.channelId = channelId;
        this.content = content;
        this.attachmentIds = attachmentIds;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public static Message of(UUID userId, UUID channelId, String content) {
        return new Message(userId, channelId, content, null);
    }

    public static Message of(UUID userId, UUID channelId, String content, List<UUID> attachmentIds) {
        return new Message(userId, channelId, content, attachmentIds);
    }

    public void update(String content) {
        this.content = content;
        this.updatedAt = Instant.now();
    }

    @Override
    public String toString() {
        return "[Message] {id=" + id + ", userId=" + userId + ", channelId=" + channelId +
                ", \n\tcontent='" + content + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt +  "'}" ;
    }
}
