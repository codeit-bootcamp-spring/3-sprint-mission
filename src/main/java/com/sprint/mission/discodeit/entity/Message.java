package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message {
    private UUID id;
    private Long createdAt;
    private Long updateAt;
    private String message;


    public Message(String message) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updateAt = System.currentTimeMillis();
        this.message = message;
    }

    public void updateMessage(String message) {
        this.message = message;
        this.updateAt = System.currentTimeMillis();
    }

    public UUID getId() {
        return id;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdateAt() {
        return updateAt;
    }

    public String getMessage() {
        return message;
    }
}
