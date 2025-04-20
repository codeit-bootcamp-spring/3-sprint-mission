package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public class Message extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    private String msgContent;
    private final UUID senderId;
    private final UUID channelId;

    public Message(String msgContent, UUID senderId, UUID channelId) {
        this.msgContent = msgContent;
        this.senderId = senderId;
        this.channelId = channelId;
    }

    public void updateMsgContent(String msgContent) {
        this.msgContent = msgContent;
        updateTime();
    }

    public UUID getSenderId() { return senderId; }

    public UUID getChannelId() {
        return channelId;
    }

    public boolean isUpdated() {
        return getUpdatedAt() != getCreatedAt();
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
