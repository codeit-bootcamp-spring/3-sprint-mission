package com.sprint.mission.discodeit.entity;

import java.util.Date;
import java.util.UUID;

public class Message extends BaseEntity {
    private String msgContent;
    private final UUID channelId;

    public Message(String msgContent, UUID senderId, UUID channelId) {
        this.msgContent = msgContent;
        this.channelId = channelId;
    }

    public void updateMsgContent(String msgContent) {
        this.msgContent = msgContent;
        updateTime();
    }

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
        return sb.toString();
    }
}
