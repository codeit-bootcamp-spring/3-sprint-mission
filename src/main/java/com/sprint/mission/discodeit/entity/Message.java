package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.common.Period;

import java.io.Serializable;
import java.util.UUID;

public class Message extends Period implements Serializable {
    private static final long serialVersionUID = 1L;

    private User sender;
    private String content;

    public UUID getId() {
        return super.getId();
    }

    public Long getCreatedAt() {
        return super.getCreatedAt();
    }

    public Long getUpdatedAt() {
        return super.getUpdatedAt();
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
        update();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
        update();
    }

    public Message(User sender, String content) {
        super();
        this.sender = sender;
        this.content = content;
    }
}
