package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.UUID;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    // 필드 정의
    private final UUID messageId;
    private final Long createdAt;
    private Long updatedAt;
    private String messageContent;            // 메세지 내용
    private UUID channelId;
    private UUID authorId;

    // 생성자
    public Message(String messageContent, UUID channelId, UUID authorId) {
        this.messageId = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.messageContent = messageContent;
        this.channelId = channelId;
        this.authorId = authorId;
    }

    // Getter

    public UUID getMessageId() {
        return messageId;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public UUID getChannelId() {
        return channelId;
    }

    public UUID getAuthorId() {
        return authorId;
    }

    // Update
    public void update(String newMessageContent) {
        boolean updated = false;
        if (newMessageContent != null && !newMessageContent.equals(this.messageContent)) {
            this.messageContent = newMessageContent;
            updated = true;
        }
        if (updated) {
            this.updatedAt = System.currentTimeMillis();
        } else {
            throw new IllegalArgumentException("No field to update");
        }
    }
}
