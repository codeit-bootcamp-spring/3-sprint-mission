package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    // 필드 정의
    private final UUID messageId;
    private final Instant createdAt;
    private Instant updatedAt;
    private String messageContent;            // 메세지 내용
    private UUID channelId;
    private UUID authorId;
    // BinaryContent 모델 참조 ID
    private List<UUID> attachmentIds;

    // 생성자
    public Message(String messageContent, UUID channelId, UUID authorId, List<UUID> attachmentIds) {
        this.messageId = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.messageContent = messageContent;
        this.channelId = channelId;
        this.authorId = authorId;
        //
        this.attachmentIds = attachmentIds;
    }

    // Update
    public void update(String newMessageContent) {
        boolean updated = false;
        if (newMessageContent != null && !newMessageContent.equals(this.messageContent)) {
            this.messageContent = newMessageContent;
            updated = true;
        }
        if (updated) {
            this.updatedAt = Instant.now();
        } else {
            throw new IllegalArgumentException("No field to update");
        }
    }
}
