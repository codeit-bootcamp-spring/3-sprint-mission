package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message {
    private final UUID messageId;
    private String content;
    private final UUID authorId;
    private final UUID channelId;
    private final long createdAt;
    private long updatedAt;

    // 생성자
    public Message(String content, UUID authorId, UUID channelId) {
        this.messageId = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
        this.content = content;
        this.authorId = authorId;
        this.channelId = channelId;
    }

    // getter
    public UUID getMessageId() {
        return messageId;
    }

    public String getContent() {
        return content;
    }

    public UUID getAuthorId() {
        return authorId;
    }

    public UUID getChannelId() {
        return channelId;
    }

    //  메시지 내용 수정
    public void updateContent(String content) {
        if (content != null && !content.isEmpty()) {
            this.content = content;
            this.updatedAt = System.currentTimeMillis(); // 수정 시간 갱신
        }
    }
    @Override
    public String toString() {
        return "Message [messageId=" + messageId + ", content=" + content + ", authorId=" + authorId + ", channelId="
                + channelId + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + "]";
    }
}
