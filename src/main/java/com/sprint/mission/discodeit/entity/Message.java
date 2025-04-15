package com.sprint.mission.discodeit.entity;

import java.time.Instant;
import java.util.UUID;

public class Message {
    private final UUID id;
    private final long createdAt;
    private long updatedAt;

    private String text;
    private final User sender;

    public Message(String text, User sender) {
        this.text = text;
        this.sender = sender;
        // for fixed unique id
        this.id = UUID.nameUUIDFromBytes(sender.getName().concat(text).getBytes());
        this.createdAt = Instant.now().getEpochSecond();
        this.updatedAt = Instant.now().getEpochSecond();
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
    public void update(String text) {
        // TODO: add setter method for field
        this.text = text;
        this.updatedAt = Instant.now().getEpochSecond();
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", text='" + text + '\'' +
                ", sender=" + sender.getName() +
                '}';
    }
}
