package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.common.Period;
import lombok.Getter;

import java.io.Serializable;
import java.util.UUID;

@Getter
public class Message extends Period implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID sender;
    private String content;

    public void setSender(UUID sender) {
        this.sender = sender;
        update();
    }

    public void setContent(String content) {
        this.content = content;
        update();
    }

    public Message(UUID sender, String content) {
        super();
        this.sender = sender;
        this.content = content;
    }
}
