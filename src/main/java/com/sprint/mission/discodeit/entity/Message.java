package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.*;

@Getter
public class Message implements Serializable {

    @Serial
    private static final long serialVersionUID = 5140283631663474458L;

    private final UUID id;
    private final Instant createdAt;
    private Instant updatedAt;
    private String msgContent;
    private final UUID authorId;
    private final UUID channelId;
    private final List<UUID> attachmentIds = new ArrayList<>();

    public Message(String msgContent, UUID authorId, UUID channelId) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = createdAt;
        this.msgContent = msgContent;
        this.authorId = authorId;
        this.channelId = channelId;
    }

    public void updateTime() {
        this.updatedAt = Instant.now();
    }

    public void updateMsgContent(String msgContent) {
        this.msgContent = msgContent;
        updateTime();
    }

    public boolean isUpdated() {
        return !Objects.equals(getUpdatedAt(), getCreatedAt());
    }

    public void addAttachment(UUID attachmentId) {
        this.attachmentIds.add(attachmentId);
        updateTime();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Instant displayTime = isUpdated() ? getUpdatedAt() : getCreatedAt();
        sb.append("[").append(Date.from(displayTime)).append("] ");
        sb.append(msgContent);
        if (isUpdated()) {
            sb.append(" (수정됨)");
        }
        sb.append(" [").append(authorId).append("] ");
        return sb.toString();
    }
}
