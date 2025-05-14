package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.common.Period;
import com.sprint.mission.discodeit.entity.dto.user.UserCreateDto;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
public class User extends Period implements Serializable {
    private static final long serialVersionUID = 1L;

    private String userName;
    private String password;
    private Set<Channel> channels;
    private byte[] profileImage;
    private boolean isOnline;

    public User() {
    }

    public User(UserCreateDto userCreateDto) {
        super();
        this.userName = userCreateDto.getUsername();
        this.password = userCreateDto.getPassword();
        this.channels = new HashSet<>();
        this.isOnline = true;
    }

    public void setUserName(String userName) {
        this.userName = userName;
        update();
    }

    public void setPassword(String password) {
        this.password = password;
        update();
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
