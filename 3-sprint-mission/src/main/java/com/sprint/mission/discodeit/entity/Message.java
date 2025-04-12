package com.sprint.mission.discodeit.entity;

import java.text.SimpleDateFormat;
import java.util.UUID;

public class Message {
    private String text;
    private final UUID id;
    private final String sendername;
    private final String channel;
    private final Long createdAt;
    private Long updatedAt;


    public Message(String text, User user, Channel channel) {
        this.text = text;
        this.id = UUID.randomUUID();
        this.sendername = user.getName();
        this.channel = channel.getName();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    public UUID getId() {
        return id;
    }
    public String getText() {return text;}
    public String getSender() {return sendername;}

    // Date 타입 포매팅
    public String getCreatedAt() {
        String formattedCreatedTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(createdAt);
        return formattedCreatedTime;}
    public String getUpdatedAt() {
        String formattedUpdatedTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(updatedAt);
        return formattedUpdatedTime;}

    @Override
    public String toString() {
        return "[channel=" + channel + "] " +
                "sender=" + sendername + " : " +
                "Message=" + text + " " +
                // 포매팅된 date 사용
                "[createdAt=" + getCreatedAt() + "]" +
                "[updatedAt=" + getUpdatedAt() + "]"
                ;
    }

    public void updateById(UUID id, String text) {
        this.text = text;
    }
    public void updateDateTime() {
        this.updatedAt = System.currentTimeMillis();
    }
}
