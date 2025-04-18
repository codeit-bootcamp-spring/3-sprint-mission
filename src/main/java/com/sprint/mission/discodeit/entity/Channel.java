package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.*;

public class Channel implements Serializable {
    private static final long serialVersionUID = 1L;
    private final UUID id;
    private final Long createdAt;
    private Long updatedAt;
    private String channelName; // 채널명
    private UUID channelMaster; // 채널 주인
    private String description; // 채널 설명
    private final List<User> userList;
    private final List<Message> messageList;

    public Channel(String channelName, User user, String description, List<User> userList, List<Message> messageList) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.channelName = channelName;
        this.channelMaster = user.getId();
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

    public UUID getChannelMaster() {
        return channelMaster;
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

    public void updateChannelMaster(User user) {
        this.channelMaster = user.getId();
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
                "  channelMaster='" + channelMaster + "', \n" +
                "  description='" + description + "',\n" +
                "  userList=" + userList.stream().map(User::getId).toList() + ",\n" +
                "  messageList=" + messageList.stream().map(Message::getId).toList() + "\n" +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Channel channel = (Channel) o;
        return Objects.equals(id, channel.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
