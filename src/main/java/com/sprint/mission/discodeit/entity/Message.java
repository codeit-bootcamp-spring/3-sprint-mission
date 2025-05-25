package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class Message implements Serializable {

    private final UUID messageId;
    private String content;
    private final UUID authorId;
    private final UUID channelId;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<UUID> attachmentIds;
    private static final long serialVersionUID = 1L;

    // 생성자
    public Message(String content, UUID authorId, UUID channelId) {
        this.messageId = UUID.randomUUID();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
        this.content = content;
        this.authorId = authorId;
        this.channelId = channelId;
    }

    // 메시지 내용 수정
    public void updateContent(String newContent) {
        if (newContent != null && !newContent.isEmpty()) {
            this.content = newContent;
            this.updatedAt = LocalDateTime.now();
        }
    }

    // attachmentIds 설정 (setter 추가)
    public void setAttachmentIds(List<UUID> attachmentIds) {
        this.attachmentIds = attachmentIds;
    }

    @Override
    public String toString() {
        return "Message [messageId=" + messageId + ", content=" + content + ", authorId=" + authorId + ", channelId="
                + channelId + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + "]";
    }

}
