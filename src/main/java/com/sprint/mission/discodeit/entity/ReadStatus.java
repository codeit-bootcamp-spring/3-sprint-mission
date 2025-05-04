package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.UUID;

public class ReadStatus implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID id;            // 고유 식별자
    private final UUID userId;        // 메시지를 읽은 사용자 ID
    private final UUID messageId;     // 읽은 메시지 ID
    private final long readAt;        // 읽은 시각 (timestamp)

    public ReadStatus(UUID userId, UUID messageId) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.messageId = messageId;
        this.readAt = System.currentTimeMillis(); // 읽은 시각을 현재 시간으로 저장
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getMessageId() {
        return messageId;
    }

    public long getReadAt() {
        return readAt;
    }

    @Override
    public String toString() {
        return "ReadStatus{" +
                "id=" + id +
                ", userId=" + userId +
                ", messageId=" + messageId +
                ", readAt=" + readAt +
                '}';
    }
}