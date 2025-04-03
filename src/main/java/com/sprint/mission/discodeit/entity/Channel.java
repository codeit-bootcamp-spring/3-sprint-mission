package com.sprint.mission.discodeit.entity;

import java.util.*;

public class Channel {
    private final UUID id;
    private final Long createdAt;
    private Long updatedAt;
    private String channelName; // 채널명
    private String description; // 채널 설명
    private final List<User> userList;
    private final List<Message> messageList;

    public Channel(String channelName, String description, List<User> userList, List<Message> messageList) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.channelName = channelName;
        this.description = description;
        this.userList = userList;
        this.messageList = messageList;
    }

    public UUID getId() {
        return id;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public String getChannelName() {
        return channelName;
    }

    public String getDescription() {
        return description;
    }

    public List<User> getUserList() {
        return userList;
    }

    public List<Message> getMessageList() {
        return messageList;
    }
    
    public void updateChannelName(String channelName) {
        this.channelName = channelName;
        this.updatedAt = System.currentTimeMillis();
    }

    public void updateDescription(String description) {
        this.description = description;
        this.updatedAt = System.currentTimeMillis();
    }

    public void updateUserList(User user) {
        this.userList.add(user);
    }

    public void updateMessageList(Message message) {
        this.messageList.add(message);
    }

    @Override
    public String toString() {
        return "Channel {\n" +
                "  id=" + id + ",\n" +
                "  createdAt=" + createdAt + ",\n" +
                "  updatedAt=" + updatedAt + ",\n" +
                "  channelName='" + channelName + "',\n" +
                "  description='" + description + "',\n" +
                "  userList=" + userList.stream().map(User::getId).toList() + ",\n" +
                "  messageList=" + messageList.stream().map(Message::getId).toList() + "\n" +
                '}';
    }

}
