package com.sprint.mission.discodeit.entitiy;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    private UUID id;
    private final Instant createdAt;
    private Instant updatedAt;
    private String emoticon;
    private String picture;
    private String text;

    public Message(String emoticon, String picture, String text) {
        this.emoticon = emoticon;
        this.picture = picture;
        this.text = text;
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
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



    public void updateId(UUID id) {
        this.id = id;
    }

    public void updateUpdatedAt(Instant updatedAt) {
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


}
