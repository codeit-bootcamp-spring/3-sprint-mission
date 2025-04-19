package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.entity
 * fileName       : Message
 * author         : doungukkim
 * date           : 2025. 4. 3.
 * description    : message entity
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 3.        doungukkim       최초 생성
 */
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private Long createdAt;
    private Long updatedAt;
    private String message;
    private UUID senderId;
    private UUID channelId;

    public Message(UUID senderId, UUID channelId, String message) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.senderId = senderId;
        this.channelId = channelId;
        this.message = message;
    }

    public UUID getSenderId() {
        return senderId;
    }

    public void setSenderId(UUID senderId) {
        this.senderId = senderId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
        this.updatedAt = System.currentTimeMillis();
    }


    public UUID getChannelId() {
        return channelId;
    }

    public void setChannelId(UUID channelId) {
        this.channelId = channelId;
        this.updatedAt = System.currentTimeMillis();
    }

    public UUID getId() {
        return id;
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
        return "Message{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", message='" + message + '\'' +
                ", senderId=" + senderId +
                ", channelId=" + channelId +
                '}';
    }
}

