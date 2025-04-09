package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message {
    private String text;
    private final String id;
    private final String sender;
    private final String channel;
    private final Long createdAt;
    private Long updatedAt;


    public Message(String text, User user, Channel channel) {
        this.text = text;
        this.id = UUID.randomUUID().toString();
        this.sender = user.getName();
        this.channel = channel.getName();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    public String getId() {
        return id;
    }
    public String getText() {return text;}
    public String getSender() {return sender;}

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public String toString() {
        return "[channel=" + channel + "] " +
                "sender=" + sender + " : " +
                "Message=" + text + " " +
                "[createdAt=" + createdAt + "]" +
                "[updatedAt=" + updatedAt + "]"
                ;
    }

    public void updateById(String id, String text) {
        this.text = text;
    }
    public void updateDateTime() {
        this.updatedAt = System.currentTimeMillis();
    }
}
