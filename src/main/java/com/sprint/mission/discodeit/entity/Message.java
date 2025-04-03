package com.sprint.mission.discodeit.entity;

import java.time.Instant;
import java.util.UUID;

public class Message {
    private UUID id;
    private long createdAt;
    private long updatedAt;

    private String text;
    private User sender;

    public Message(String text, User sender) {
        this.text = text;
        this.sender = sender;
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now().getEpochSecond();
    }

    public UUID getId() {
        return id;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public String getText() {
        return text;
    }

    public User getSender() {
        return sender;
    }

    // 필드를 수정하는 update 함수를 정의하세요.
    public void update() {
        // TODO: need to update this.updatedAt
    }
}
