package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Getter
public class Message extends BaseEntity {
    // userId channelId content
    private User user;
    private Channel channel;
    private String content;

    public Message(User user, Channel channel, String content) {
        this.user = user;
        this.channel = channel;
        this.content = content;
    }

    public void update(String content) {
        this.content = content;
        setUpdatedAt();
    }

    @Override
    public String toString() {
        return "[Message] " +
                " time: " + getUpdatedAt() +
                ", userId: " + user.getUserName() +
                ", channelId: " + channel.getChannelName() +
                ", content: '" + content + '\'' +
                '}' + '\n';
    }
}
