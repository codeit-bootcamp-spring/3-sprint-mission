package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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


    public Message(String content, UUID userId, UUID channelId) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        //
        this.content = content;
        this.userId = userId;
        this.channelId = channelId;
    }

    public void update(String content) {
        boolean anyValueUpdated = false;
        if (content != null && !content.equals(this.content)) {
            this.content = content;
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
