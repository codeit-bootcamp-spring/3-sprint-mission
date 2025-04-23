package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.UUID;

public class Message implements Serializable {
    private static int counter = 1;

    private int number;
    private UUID id;
    private long createdAt;
    private long updateAt;
    private String message;

    public Message(String message) {
        this.number = counter++;
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updateAt = System.currentTimeMillis();
        this.message = message;
    }

    public void updateMessage(String message) {
        this.number = counter++;
        this.message = message;
        this.updateAt = System.currentTimeMillis();
    }

    public UUID getId() {
        return id;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdateAt() {
        return updateAt;
    }

    public String getMessage() {
        return message;
    }

    public int getNumber() {
        return number;
    }

    public static void setCounter(int counter) {
        Message.counter = counter;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdateAt(long updateAt) {
        this.updateAt = updateAt;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Message{" +
                "number=" + number +
                ", id=" + id +
                ", createdAt=" + createdAt +
                ", updateAt=" + updateAt +
                ", message='" + message + '\'' +
                '}';
    }
}