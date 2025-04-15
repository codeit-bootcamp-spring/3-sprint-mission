package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Message implements Serializable {
    private final UUID id;
    private final long createdAt;
    private long updatedAt;

    private String text;
    private final User sender;
    private final Channel channel;

    public Message(String text, User sender, Channel channel) {
        this.text = text;
        this.sender = sender;
        this.channel = channel;
        this.createdAt = Instant.now().getEpochSecond();
        this.updatedAt = Instant.now().getEpochSecond();
        this.id = UUID.randomUUID();

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

    public Channel getChannel() {
        return channel;
    }

    // 필드를 수정하는 update 함수를 정의하세요.
    public Message update(String text) {
        // TODO: add setter method for field
        this.text = text;
        this.updatedAt = Instant.now().getEpochSecond();

        return this;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.systemDefault());

        String createdAtFormatted = formatter.format(Instant.ofEpochSecond(createdAt));
        String updatedAtFormatted = formatter.format(Instant.ofEpochSecond(updatedAt));

        return "💬 Message {\n" +
                " id         = " + id + "\n" +
                " createdAt  = " + createdAtFormatted + "\n" +
                " updatedAt  = " + updatedAtFormatted + "\n" +
                " text       = '" + text + "'\n" +
                " sender     = " + sender.getName() + "\n" +
                "}";
    }
}
