package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class User extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    private String userName;        // 유저 이름
    private String userId;          // 유저 ID (검색용 유니크값)
    private List<Channel> joinChannelList;      // 유저가 참여중인 채널리스트
    private boolean isActive;   // 활성여부 (탈퇴시 false)

    public User(String userName, String userId) {
        super();
        this.userName = userName;
        this.userId = userId;
        this.joinChannelList = new ArrayList<>();
        this.isActive = true;
    }

    public String getUserName() {
        return userName;
    }

    public void updateUserName(String userName) {
        this.userName = userName;
        this.updateTimestamp();
    }

    public String getUserId() {
        return userId;
    }

    public List<Channel> getJoinChannelList() {
        return joinChannelList;
    }

    public void updateJoinChannelList(Channel joinChannel) {
        this.joinChannelList.add(joinChannel);
        this.updateTimestamp();
    }

    public void deleteJoinChannelList(Channel joinChannel) {
        this.joinChannelList.remove(joinChannel);
        this.updateTimestamp();
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void updateIsActive() {
        this.isActive = false;
        this.updateTimestamp();
    }


    @Override
    public String toString() {
        return "User{" +
                "Id='" +getId() + '\'' +
                ", userName='" + userName + '\'' +
                ", userId='" + userId + '\'' +
                (isActive ? "" : "(탈퇴)") +
                ", joinChannelList=" + joinChannelList.stream().map(Channel::getChannelName).toList() +
                ", createdAt=" + new SimpleDateFormat("yy/MM/dd HH:mm:ss").format(getCreatedAt()) +
                ", updatedAt=" + new SimpleDateFormat("yy/MM/dd HH:mm:ss").format(getUpdatedAt()) +
                "}\n";
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(getId(), user.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
