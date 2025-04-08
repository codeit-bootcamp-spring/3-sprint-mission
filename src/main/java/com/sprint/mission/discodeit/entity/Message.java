package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message extends BaseEntity {
    private String msgContent;
    private final UUID senderId;
    private final UUID channelId;

    public Message(String msgContent, UUID senderId, UUID channelId) {
        this.msgContent = msgContent;
        this.senderId = senderId;
        this.channelId = channelId;
    }

    public String getMsgContent() {
        return msgContent;
    }

    public void updateMsgContent(String msgContent) {
        this.msgContent = msgContent;
        updateTime();
    }

    public UUID getChannelId() {
        return channelId;
    }

    @Override
    public String toString() {
        return "Message{senderId=" + senderId +
                ", channelId=" + channelId +
                ", msgContent=" + msgContent +
                ", createdAt=" + getCreatedAt() +
                ", updatedAt=" + getUpdatedAt() + '}';
    }
}
