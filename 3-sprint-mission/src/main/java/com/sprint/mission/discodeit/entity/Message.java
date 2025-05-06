package com.sprint.mission.discodeit.entity;

import lombok.Data;
import lombok.Getter;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.UUID;

@Data
@Getter
public class Message implements Serializable {
    private String text;
    private final UUID id;
    private final UUID authorId;
    private final UUID channelId;
    private final Instant createdAt;
    private Instant updatedAt;
    private boolean updated;


    public Message(UUID currentUserId, UUID currentChannelId, String text) {
        this.id = UUID.randomUUID();
        this.authorId = currentUserId;
        this.channelId = currentChannelId;
        this.text = text;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.updated = false;
    }

    // Date 타입 포매팅
    public String getCreatedAt() {
        String formattedCreatedTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(createdAt);
        return formattedCreatedTime;
    }

    // Date 타입 포매팅
    public String getUpdatedAt() {
        String formattedUpdatedTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(updatedAt);
        return formattedUpdatedTime;
    }

    @Override
    public String toString() {
        if (!this.updated) {

            return //"[channel=" + getChannel() + "] " +
                    "sender=" + getAuthorId() + " : " +
                    "Message=" + text + " " +
                    // 포매팅된 date 사용
                    "[createdAt=" + getCreatedAt() + "]"
                    ;
        } else {

            return //"[channel=" + getChannel() + "] " +
                    "sender=" + getAuthorId() + " : " +
                    "Message=" + text + " " +
                    // 포매팅된 date 사용
                    "[createdAt=" + getCreatedAt() + "]" +
                    "[updatedAt=" + getUpdatedAt() + "](수정됨)"
                    ;
        }

    }

    public void updateText(String text) {
        this.text = text;
        updateDateTime();
        updated();
    }

    public void updateDateTime() {
        this.updatedAt = Instant.now();
    }

    public void updated() {
        this.updated = true;
    }

}
