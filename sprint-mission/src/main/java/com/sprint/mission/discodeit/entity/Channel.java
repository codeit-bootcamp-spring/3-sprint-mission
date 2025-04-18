package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Channel implements Serializable {
    // 필드 선언부
    private static final long serialVersionUID = 1L;
    private final UUID id;
    private final long createdAt;
    private long updatedAt;
    private String channelName;
    private List<UUID> messageList; // 채널에서 받은 메시지 리스트
    private List<UUID> userList;

    // 생성자
    public Channel(String channelName) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = createdAt;
        this.channelName = channelName;
        this.messageList = new ArrayList<>();
        this.userList = new ArrayList<>();
    }

    // getter 함수부
    public String getChannelName() {
        return channelName;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public UUID getId() {
        return id;
    }

    public List<UUID> getMessageList() {
        return messageList;
    }

    public List<UUID> getUserList() { return userList; }

    public long getUpdatedAt() {
        return updatedAt;
    }


    public UUID addMessageToChannel(UUID messageUUID){
        messageList.add(messageUUID);
        return messageUUID;
    }

    public UUID addUserToChannel(UUID userUUID){
        userList.add(userUUID);
        return userUUID;
    }

    // channelName 업데이트 함수
    public void updateChannelName(String channelName){
        this.channelName = channelName;
        this.updatedAt = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return  " 채널명 : " + channelName +
                " / 메시지 리스트 : " + messageList +
                " / 유저 리스트 : " + userList
                +"\n";
    }
}
