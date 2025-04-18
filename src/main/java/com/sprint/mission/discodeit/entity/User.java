package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.*;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private final UUID id;
    private final Long createdAt;
    private Long updatedAt;
    private String name; // 이름
    private String email; // 이메일
    private String password; // 비밀번호
    private String introduction; // 소개
    private boolean status; // 접속 여부
    private List<UUID> friends; // 친구 목록
    private List<Channel> channels; // 속한 채널 목록
    private List<Message> messages; // 보낸 메시지

    public User(String name, String email, String password, String introduction,
                boolean status, List<UUID> friends, List<Channel> channels, List<Message> messages) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.name = name;
        this.email = email;
        this.password = password;
        this.introduction = introduction;
        this.status = status;
        this.friends = friends;
        this.channels = channels;
        this.messages = messages;
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

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getIntroduction() {
        return introduction;
    }

    public boolean isStatus() {
        return status;
    }

    public List<UUID> getFriends() {
        return friends;
    }

    public List<Channel> getChannels() {
        return channels;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void updateName(String name) {
        this.name = name;
        this.updatedAt = System.currentTimeMillis();
    }

    public void updateEmail(String email) {
        this.email = email;
        this.updatedAt = System.currentTimeMillis();
    }

    public void updatePassword(String password) {
        this.password = password;
        this.updatedAt = System.currentTimeMillis();
    }

    public void updateIntroduction(String introduction) {
        this.introduction = introduction;
        this.updatedAt = System.currentTimeMillis();
    }

    public void updateStatus(boolean status) {
        this.status = status;
    }

    public void updateFriends(List<UUID> friends) {
        this.friends = friends;
    }

    public void updateChannels(List<Channel> channels) {
        this.channels = channels;
    }

    public void updateMessages(List<Message> messages) {
        this.messages = messages;
    }

    @Override
    public String toString() {
        return "User {\n" +
                "  id=" + id + ",\n" +
                "  createdAt=" + createdAt + ",\n" +
                "  updatedAt=" + updatedAt + ",\n" +
                "  name='" + name + "',\n" +
                "  email='" + email + "',\n" +
                "  password='" + password + "',\n" +
                "  introduction='" + introduction + "',\n" +
                "  status=" + status + ",\n" +
                "  friends=" + friends + ",\n" +
                "  channels=" + channels.stream().map(Channel::getId).toList() + "\n" +
                "  messages=" + messages.stream().map(Message::getId).toList() + "\n" +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
