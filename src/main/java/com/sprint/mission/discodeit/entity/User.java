package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.common.Period;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class User extends Period implements Serializable {
    private static final long serialVersionUID = 1L;

    private String userName;
    private String password;
    private Set<Channel> channels;

    public User() {
    }

    public User(String userName, String password) {
        super();
        this.userName = userName;
        this.password = password;
        this.channels = new HashSet<>();
    }

    public UUID getId() {
        return super.getId();
    }


    public Long getCreatedAt() {
        return super.getCreatedAt();
    }


    public Long getUpdatedAt() {
        return super.getUpdatedAt();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
        update();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        update();
    }

    public Set<Channel> getChannels() {
        return channels;
    }

    public void setChannels(Set<Channel> channels) {
        this.channels = channels;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + super.getId() +
                ", userName='" + userName + '\'' +
                ", createdAt=" + super.getCreatedAt() +
                ", updatedAt=" + super.getUpdatedAt() +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return Objects.equals(this.getId(), user.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    public void joinChannel(Channel channel) {
        channels.add(channel);
        if (!channel.getMembers().contains(this)) {
            channel.addMember(this); // 양방향 연결
        }
    }
}
