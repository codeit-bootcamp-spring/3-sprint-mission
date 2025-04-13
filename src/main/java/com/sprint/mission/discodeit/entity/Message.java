package com.sprint.mission.discodeit.entity;


import java.util.UUID;

public class Message extends Common {
    private final UUID userId;
    private final UUID channelId;
    private String content;

    public Message(UUID userId, UUID channelId, String content) {
        super();
        this.userId = userId;
        this.channelId = channelId;
        this.content = content;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getChannelId() {
        return channelId;
    }

    public String getContent() {
        return content;
    }

    public void updateContent(String newContent) {
        if (newContent != null && !newContent.isEmpty()) {
            this.content = newContent;
            super.updateUpdatedAt();
        }
    }

    @Override
    public String toString() {
        return "Message{" +
                "content='" + getContent() + '\'' +
                ", user='" + getUserId() + '\'' +
                ", channel='" + getChannelId() + '\'' +
                ", id='" + getId() + '\'' +
                ", createdAt='" + getCreatedAt() + '\'' +
                ", updatedAt='" + getUpdatedAt() + '\'' +
                '}';
    }
}
