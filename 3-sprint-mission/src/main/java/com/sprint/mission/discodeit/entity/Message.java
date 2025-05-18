package com.sprint.mission.discodeit.entity;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@ToString
public class Message implements Serializable {
    private final UUID id;
    private final UUID authorId;
    private final UUID channelId;
    private String text;
    private List<UUID> attachmentIds;
    private final Instant createdAt;
    private Instant updatedAt;
    private boolean updated;

    @Builder
    public Message(UUID currentUserId, UUID currentChannelId, String text, List<UUID> attachmentIds) {
        this.id = UUID.randomUUID();
        this.authorId = currentUserId;
        this.channelId = currentChannelId;
        this.text = text;
        this.attachmentIds = attachmentIds;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.updated = false;
    }

    public void updateText(String text) {
        this.text = text;
        updateDateTime();
        updated();
    }

    public void updateDateTime() {
        this.updatedAt = Instant.now();
    }

    public void updated() {
        this.updated = true;
    }

}
