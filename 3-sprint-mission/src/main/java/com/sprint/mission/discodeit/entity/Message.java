package com.sprint.mission.discodeit.entity;

public class Message extends BaseEntity {
    // userId channelId content
    private String userId;
    private String channelId;
    private String content;

    public Message(String userId, String channelId, String content) {
        super();
        this.userId = userId;
        this.channelId = channelId;
        this.content = content;
    }

    public String getUserId() {
        return userId;
    }

    public String getChannelId() {
        return channelId;
    }

    public String getContent() {
        return content;
    }

    public void update(String content) {
        this.content = content;
        setUpdatedAt();
    }
}
