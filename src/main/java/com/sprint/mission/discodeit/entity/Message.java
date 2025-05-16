package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Message implements java.io.Serializable{

    //serialVersionUID 필드 추가
    @Serial
    private static final long serialVersionUID = 1L;

    private UUID messageId;
    private Instant createdAt;
    private Instant updatedAt;
    private UUID channelId;
    private UUID senderId;
    private String messageContent;
    private LocalDateTime timestamp;
    private List<UUID> attachedFileIds;


    public Message() {
    }

    public Message(UUID channelId, UUID senderId, String messageContent) {
        this.messageId = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.channelId = channelId;
        this.senderId = senderId;
        this.messageContent = messageContent;
        this.timestamp = LocalDateTime.now();
        this.attachedFileIds = new ArrayList<>();
    }

    public void updateId(UUID messageId) {
        this.messageId = messageId;
    }

    public void updateCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void updateUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void updateChannelId(UUID channelId) {
        this.channelId = channelId;
    }

    public void updateSenderId(UUID senderId) {
        this.senderId = senderId;
    }

    public void updateMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public void updateTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.of("Asia/Seoul"));

        return "Message{" +
                "messageId=" + messageId +
                ", createdAt=" + formatter.format(createdAt) +
                ", updatedAt=" + formatter.format(updatedAt) +
                ", channelId=" + channelId +
                ", senderId=" + senderId +
                ", messageContent='" + messageContent + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
