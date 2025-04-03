package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message {
    private final UUID id;
    private final Long createdAt;
    private Long updatedAt;
    private String content; // 내용
    private final UUID senderId; // 보낸 사람의 id
    private final UUID channelId; // 메시지를 보낸 채널 id

    public Message(String content, User user, Channel channel) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.content = content;
        this.senderId = user.getId();
        this.channelId = channel.getId();
    }

    public UUID getId() {
        return id;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public String getContent() {
        return content;
    }

    public UUID getSenderId() {
        return senderId;
    }

    public UUID getChannelId() {
        return channelId;
    }

    public void updateContent(String content) {
        this.content = content;
        this.updatedAt = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "Message {\n" +
                "  id=" + id + ",\n" +
                "  createdAt=" + createdAt + ",\n" +
                "  updatedAt=" + updatedAt + ",\n" +
                "  content='" + content + "',\n" +
                "  senderId=" + senderId + ",\n" +
                "  channelId=" + channelId + "\n" +
                '}';
    }
}
