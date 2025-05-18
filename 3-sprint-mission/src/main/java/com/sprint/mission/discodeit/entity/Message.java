package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.UUID;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;
    private String text;
    private final UUID id;
    private final UUID sender;
    private final UUID channel;
    private final Long createdAt;
    private Long updatedAt;
    private boolean updated;


    public Message(UUID currentUserId, UUID currentChannelId, String text) {
        this.id = UUID.randomUUID();
        this.sender = currentUserId;
        this.channel = currentChannelId;
        this.text = text;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
        this.updated = false;
    }

    public UUID getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    // Sender 이름 반환
    public UUID getSender() {
        return sender;
    }

    // CHannel 이름 반환
    public UUID getChannel() {
        return channel;
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
                    "sender=" + getSender() + " : " +
                    "Message=" + text + " " +
                    // 포매팅된 date 사용
                    "[createdAt=" + getCreatedAt() + "]"
                    ;
        } else {

            return //"[channel=" + getChannel() + "] " +
                    "sender=" + getSender() + " : " +
                    "Message=" + text + " " +
                    // 포매팅된 date 사용
                    "[createdAt=" + getCreatedAt() + "]" +
                    "[updatedAt=" + getUpdatedAt() + "](수정됨)"
                    ;
        }

    }

    public void updateText(UUID id, String text) {
        this.text = text;
        updateDateTime();
        updated();
    }

    public void updateDateTime() {
        this.updatedAt = System.currentTimeMillis();
    }

    public void updated() {
        this.updated = true;
    }

}
