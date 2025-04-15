package com.sprint.mission.discodeit.entity;


import java.util.UUID;

public class Message extends Base {
    private User user;
    private Channel channel; // 이걸 Channel 클래스와 어떻게 연결시키지
    private String content; // message는 결국 HashSet이야

    public Message(User user, Channel channel, String content) {
        super();
        this.user = user;
        this.channel = channel;
        this.content = content;
    }


    // Getter Methods

    public User getUser() { return user; }

    public Channel getChannel() { return channel; }

    public String getContent() {
        return content;
    }


    // Update Method

    public void updateUser(UUID id) {
        this.user = super.getId();
        super.updateUpdatedAt();
    }

    public void updateChannel(Channel channel) {
        this.channel = channel;
        super.updateUpdatedAt();
    }

    public void updateContent(String message) {
        this.content = content;
        super.updateUpdatedAt();
    }

    @Override
    public String toString() {
        return "Message{" +
                "message='" + getContent() + '\'' +
                ", user='" + getUser().getName() + '\'' +
                ", channel='" + getChannel().getChannelName() + '\'' +
                ", id='" + getId() + '\'' +
                ", createdAt='" + getCreatedAt() + '\'' +
                ", updatedAt='" + getUpdatedAt() + '\'' +
                '}';
}
