package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
public class Message implements Serializable {
    private static final long serialVersionUID = -6322726657551422728L;

    private UUID id;
    private Instant createdAt;
    private Instant updatedAt = null;
    //
    private String text;
    private UUID channelId;
    private UUID userId;
    private List<UUID> contentIds = null;


    public Message(String text, UUID channelId, UUID userId) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        //
        this.text = text;
        this.channelId = channelId;
        this.userId = userId;
    }
    public Message(String text, UUID channelId, UUID userId, List<UUID> contentIds) {
        this(text,channelId,userId);
        this.contentIds = contentIds;
    }
    public void update(String newText){
        if (newText != null && !newText.equals(this.text)) {
            this.text = newText;
            if(updatedAt== null) {
                this.text += "*** 수정됨 ***";
            }
            this.updatedAt = Instant.now();
        }
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", content='" + text + '\'' +
                ", channelId=" + channelId +
                ", authorId=" + userId +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
