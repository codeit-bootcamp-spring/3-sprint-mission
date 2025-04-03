package com.sprint.mission.discodeit.entitiy;

import java.util.UUID;

public class Message {
    private UUID id;
    private Long createdAt;
    private Long updatedAt;
    private String emoticon;
    private String picture;
    private String text;

    public Message(String emoticon, String picture, String text) {
        this.emoticon = emoticon;
        this.picture = picture;
        this.text = text;
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", emoticon='" + emoticon + '\'' +
                ", picture='" + picture + '\'' +
                ", text='" + text + '\'' +
                '}';
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

    public void updateId(UUID id) {
        this.id = id;
    }

    public void updateCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public void updateUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void updateEmoticon(String emoticon) {
        this.emoticon = emoticon;
    }

    public void updatePicture(String picture) {
        this.picture = picture;
    }

    public void updateText(String text) {
        this.text = text;
    }

    public String getEmoticon() {
        return emoticon;
    }

    public String getPicture() {
        return picture;
    }

    public String getText() {
        return text;
    }
}
