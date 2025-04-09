package com.sprint.mission.discodeit.entity;


public class Message extends Common {
    private final User user;
    private final Channel channel;
    private String content;

    public Message(User user, Channel channel, String content) {
        super();
        this.user = user;
        this.channel = channel;
        this.content = content;
    }

    public User getUser() {
        return user;
    }

    public Channel getChannel() {
        return channel;
    }

    public String getContent() {
        return content;
    }

    public void updateContent(String content) {
        this.content = content;
        super.updateUpdatedAt();
    }

    @Override
    public String toString() {
        return "Message{" +
                "content='" + getContent() + '\'' +
                ", user='" + getUser().getName() + '\'' +
                ", channel='" + getChannel().getName() + '\'' +
                ", id='" + getId() + '\'' +
                ", createdAt='" + getCreatedAt() + '\'' +
                ", updatedAt='" + getUpdatedAt() + '\'' +
                '}';
    }
}
