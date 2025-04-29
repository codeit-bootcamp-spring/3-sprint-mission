package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Getter
public class Message implements Serializable {
    private static final long serialVersionUID = 5140283631663474458L;

    private final UUID id;
    private final Long createdAt;
    private Long updatedAt;
    private String msgContent;
    private final UUID senderId;
    private final UUID channelId;

    public Message(String msgContent, UUID senderId, UUID channelId) {
        this.id = UUID.randomUUID();
        this.createdAt = new Date().getTime();
        this.updatedAt = new Date().getTime();
        this.msgContent = msgContent;
        this.senderId = senderId;
        this.channelId = channelId;
    }

    public void updateTime() {
        this.updatedAt = System.currentTimeMillis();
    }

    public void updateMsgContent(String msgContent) {
        this.msgContent = msgContent;
        updateTime();
    }

    public boolean isUpdated() {
        return !Objects.equals(getUpdatedAt(), getCreatedAt());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        long displayTime = isUpdated() ? getUpdatedAt() : getCreatedAt();
        sb.append("[").append(new Date(displayTime)).append("] ");
        sb.append(msgContent);
        if (isUpdated()) {
            sb.append(" (수정됨)");
        }
        sb.append(" [").append(senderId).append("] ");
        return sb.toString();
    }
}
