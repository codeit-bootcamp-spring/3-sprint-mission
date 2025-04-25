package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

public class Message implements Serializable {
    @Getter
    private static final long serialVersionUID = 1L;
    //
    @Getter
    private final UUID id;
    @Getter
    private final Instant createdAt;
    @Getter
    private Instant updatedAt;
    //
    @Getter
    private String content;
    //
    @Getter
    private final UUID userId;
    @Getter
    private final UUID channelId;
    @Getter
    private List<UUID> attachmentIds;  // BinaryContent의 id


    public Message(String content, UUID userId, UUID channelId, List<UUID> attachmentIds) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        //
        this.content = content;
        this.userId = userId;
        this.channelId = channelId;
        this.attachmentIds = attachmentIds;
    }

    public void update(String content, List<UUID> attachmentIds) {
        boolean anyValueUpdated = false;
        if (content != null && !content.equals(this.content)) {
            this.content = content;
            anyValueUpdated = true;
        }

        if (attachmentIds != null && !attachmentIds.equals(this.attachmentIds)) {
            this.attachmentIds = attachmentIds;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.updatedAt = Instant.now();
        }
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.systemDefault());

        String createdAtFormatted = formatter.format(createdAt);
        String updatedAtFormatted = formatter.format(updatedAt);

        return "💬 Message {\n" +
                " id         = " + id + "\n" +
                " createdAt  = " + createdAtFormatted + "\n" +
                " updatedAt  = " + updatedAtFormatted + "\n" +
                " content       = '" + content + "'\n" +
                " sender     = " + userId + "\n" +
                "}";
    }
}
