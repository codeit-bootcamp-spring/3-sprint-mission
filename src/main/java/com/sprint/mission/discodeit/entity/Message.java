package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.UUID;
import java.time.Instant;

import lombok.Getter;

@Getter
public class Message implements Serializable {

    private final UUID messageId;
    private String content;
    private final UUID authorId;
    private final UUID channelId;
    private final Instant createdAt;
    private Instant updatedAt;
    private static final long serialVersionUID = 1L;

    // 생성자
    public Message(String content, UUID authorId, UUID channelId) {
        this.messageId = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
        this.content = content;
        this.authorId = authorId;
        this.channelId = channelId;
    }

    //  메시지 내용 수정
    public void updateContent(String content) {
        if (content != null && !content.isEmpty()) {
            this.content = content;
            this.updatedAt = Instant.now(); // 수정 시간 갱신
        }
    }

    @Override
    public String toString() {
        return "Message [messageId=" + messageId + ", content=" + content + ", authorId=" + authorId + ", channelId="
                + channelId + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + "]";
    }
}
