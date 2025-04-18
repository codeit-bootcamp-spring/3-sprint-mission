package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Channel extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    private String channelName;     // 채널 이름
    private List<Message> messageList;      // 채널에 등록된 메시지 리스트
    private List<User> joinUserList;        // 채널에 참여중인 유저리스트

    public Channel(String channelName) {
        this.channelName = channelName;
        this.messageList = new ArrayList<>();
        this.joinUserList = new ArrayList<>();
    }

    public Channel(String channelName, List<Message> messageList, List<User> joinUserList) {
        this.channelName = channelName;
        this.messageList = messageList;
        this.joinUserList = joinUserList;
    }

    public String getChannelName() {
        return channelName;
    }

    public void updateChannelName(String channelName) {
        this.channelName = channelName;
        this.updateTimestamp();
    }

    public List<Message> getMessageList() {
        return messageList;
    }

    public void updateMessageList(Message message) {
        this.messageList.add(message);
        this.updateTimestamp();
    }


    public List<User> getJoinUserList() {
        return joinUserList;
    }

    public void updateJoinUserList(User user) {
        this.joinUserList.add(user);
        this.updateTimestamp();
    }

    public void deleteJoinUserList(User user) {
        this.joinUserList.remove(user);
        this.updateTimestamp();
    }

    @Override
    public String toString() {
        return "Channel{" +
                "chId='" + getId() + '\'' +
                ", channelName='" + channelName + '\'' +
                ", messageList=" + messageList.stream().map( m -> m.getSendUser().getUserName()+ " : " + m.getMsgContent() + (m.getIsUpdated() ? "(수정됨)" : "")  + (m.getIsDeleted() ? "(삭제된 메세지입니다.)" : "")).toList() +
                ", joinUserList=" +  joinUserList.stream().map(u -> u.getUserName() + (u.getIsActive() ? "" : "(탈퇴)")).toList() +
                ", createdAt=" + new SimpleDateFormat("yy/MM/dd HH:mm:ss").format(getCreatedAt())  +
                ", updatedAt=" + new SimpleDateFormat("yy/MM/dd HH:mm:ss").format(getUpdatedAt()) +
                "}\n";
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Channel channel = (Channel) o;
        return Objects.equals(this.getId(), channel.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
